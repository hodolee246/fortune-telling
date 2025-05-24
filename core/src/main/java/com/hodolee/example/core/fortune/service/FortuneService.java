package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import com.hodolee.example.domain.fortune.domain.Fortune;
import com.hodolee.example.domain.fortune.domain.FortuneRepository;
import com.hodolee.example.infra.fortune.external.NaverFortuneClient;
import com.hodolee.example.infra.fortune.redis.RedisFortuneResponse;
import com.hodolee.example.infra.fortune.url.UrlGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FortuneService {

    private final NaverFortuneClient naverFortuneClient;
    private final UrlGenerator urlGenerator;
    private final FortuneRepository fortuneRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, RedisFortuneResponse> redisTemplate;

    @CircuitBreaker(name = "fortuneService", fallbackMethod = "getDefaultFortuneUrl")
    @Transactional
    public String getFortuneUrl(String name, String birthDate) {
        // redis 분산락
        String lockKey = "fortune:" + name + ":" + birthDate;
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(3, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요.");
            }

            // 네이버 운세 크롤링 반환
            String fortuneText = naverFortuneClient.getFortune(birthDate);

            // url
            return saveFortuneAndReturnUrl(Fortune.builder()
                    .name(name)
                    .birthDate(birthDate)
                    .fortuneText(fortuneText)
                    .build());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("락 획득 중 예외 발생 : " + e.getMessage());
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("락 해제 실패 : {}", e.getMessage());
                }
            }
        }
    }

    private String getDefaultFortuneUrl(String name, String birthDate, Throwable t) {
        log.info("서킷브레이크 활성화 : {}", t.getMessage());
        // redis 분산락
        String lockKey = "fortune:" + name + ":" + birthDate;
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;

        try {
            acquired = lock.tryLock(3, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요.");
            }

            String defaultFortune = "하늘을 조심하세요.";

            // url
            return saveFortuneAndReturnUrl(Fortune.builder()
                    .name(name)
                    .birthDate(birthDate)
                    .fortuneText(defaultFortune)
                    .build());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("락 획득 중 예외 발생 : " + e.getMessage());
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("락 해제 실패 : {}", e.getMessage());
                }
            }
        }
    }

    public FortuneResponse getFortune(String encryptIdx) {
        // redis 캐시
        String redisKey = "fortune:result:" + encryptIdx;
        RedisFortuneResponse cached = redisTemplate.opsForValue().get(redisKey);

        // 캐시 존재 시 캐시 반환
        if (cached != null) {
            return new FortuneResponse(cached.name(), cached.birthDate(), cached.fortuneText());
        }

        Long decodeIdx = urlGenerator.getDecodedUrl(encryptIdx);
        // 저장된 운세 조회
        Fortune getFortune = fortuneRepository.findById(decodeIdx)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 운세 URL 입니다."));
        // 운세 조회 횟수 상승
        getFortune.incrementViewCount();
        // 캐시 저장
        RedisFortuneResponse cache = new RedisFortuneResponse(
                getFortune.getName(),
                getFortune.getBirthDate(),
                getFortune.getFortuneText()
        );
        redisTemplate.opsForValue().set(redisKey, cache, Duration.ofMinutes(60));

        return new FortuneResponse(getFortune.getName(), getFortune.getBirthDate(), getFortune.getFortuneText());
    }

    private String saveFortuneAndReturnUrl(Fortune fortune) {
        Fortune savedFortune = fortuneRepository.save(fortune);

        return urlGenerator.generateEncodeUrl(savedFortune.getIdx());
    }

}
