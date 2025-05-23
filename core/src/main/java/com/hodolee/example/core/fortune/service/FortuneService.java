package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import com.hodolee.example.domain.fortune.domain.Fortune;
import com.hodolee.example.domain.fortune.domain.FortuneRepository;
import com.hodolee.example.infra.fortune.external.NaverFortuneClient;
import com.hodolee.example.infra.fortune.url.UrlGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FortuneService {

    private final NaverFortuneClient naverFortuneClient;
    private final UrlGenerator urlGenerator;
    private final FortuneRepository fortuneRepository;

    public FortuneService(NaverFortuneClient naverFortuneClient, UrlGenerator urlGenerator, FortuneRepository fortuneRepository) {
        this.naverFortuneClient = naverFortuneClient;
        this.urlGenerator = urlGenerator;
        this.fortuneRepository = fortuneRepository;
    }

    @CircuitBreaker(name = "fortuneService", fallbackMethod = "getDefaultFortuneUrl")
    @Transactional
    public String getFortuneUrl(String name, String birthDate) {
        // 네이버 운세 크롤링 반환
        String fortuneText = naverFortuneClient.getFortune(birthDate);
        Fortune savedFortune = fortuneRepository.save(Fortune.builder()
                .name(name)
                .birthDate(birthDate)
                .fortuneText(fortuneText)
                .build());
        // url
        String generatedFortuneUrl = urlGenerator.generateEncodeUrl(savedFortune.getIdx());

        return generatedFortuneUrl;
    }

    private String getDefaultFortuneUrl(String name, String birthDate, Throwable t) {
        log.info("서킷브레이크 활성화 : {}", t.getMessage());
        String defaultFortune = "하늘을 조심하세요.";
        // 기본 운세 저장
        Fortune savedFortune = fortuneRepository.save(Fortune.builder()
                .name(name)
                .birthDate(birthDate)
                .fortuneText(defaultFortune)
                .build());
        // url
        String generatedFortuneUrl = urlGenerator.generateEncodeUrl(savedFortune.getIdx());

        return generatedFortuneUrl;
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

}
