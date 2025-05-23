package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import com.hodolee.example.domain.fortune.domain.Fortune;
import com.hodolee.example.domain.fortune.domain.FortuneRepository;
import com.hodolee.example.infra.fortune.external.NaverFortuneClient;
import com.hodolee.example.infra.fortune.url.UrlGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("락 획득 중 예외 발생 : " + e.getMessage());
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
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
            throw new IllegalArgumentException("락 획득 중 예외 발생 : " + e.getMessage());
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public FortuneResponse getFortune(String encryptIdx) {
        Long decodeIdx = urlGenerator.getDecodedUrl(encryptIdx);
        // 저장된 운세 조회
        Fortune getFortune = fortuneRepository.findById(decodeIdx)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 운세 URL 입니다."));
        // 운세 조회 횟수 상승
        getFortune.incrementViewCount();

        return new FortuneResponse(getFortune.getName(), getFortune.getBirthDate(), getFortune.getFortuneText());
    }

    private String saveFortuneAndReturnUrl(Fortune fortune) {
        Fortune savedFortune = fortuneRepository.save(fortune);

        return urlGenerator.generateEncodeUrl(savedFortune.getIdx());
    }

}
