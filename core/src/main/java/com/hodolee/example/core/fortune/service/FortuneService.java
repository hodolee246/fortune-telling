package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import com.hodolee.example.domain.fortune.domain.Fortune;
import com.hodolee.example.domain.fortune.domain.FortuneRepository;
import com.hodolee.example.infra.fortune.external.NaverFortuneClient;
import com.hodolee.example.infra.fortune.url.UrlGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
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

    @CircuitBreaker(name = "fortuneService", fallbackMethod = "getDefaultFortune")
    public FortuneResponse getFortune(String name, String birthDate) {
        // 네이버 운세 크롤링 반환
        String fortuneText = naverFortuneClient.getFortune(birthDate);
        Fortune savedFortune = fortuneRepository.save(Fortune.builder()
                .name(name)
                .birthDate(birthDate)
                .fortuneText(fortuneText)
                .build());
        // url
        String generatedFortuneUrl = urlGenerator.generateEncodeUrl(savedFortune.getIdx());

        return new FortuneResponse(name, birthDate, generatedFortuneUrl);
    }

    private FortuneResponse getDefaultFortune(String name, String birthDate, Throwable t) {
        log.info("서킷브레이크 활성화 : {}", t.getMessage());
        String defaultFortune = "하늘을 조심하세요.";
        return new FortuneResponse(name, birthDate, defaultFortune);
    }

}
