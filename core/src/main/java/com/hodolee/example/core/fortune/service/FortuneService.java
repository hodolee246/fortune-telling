package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import com.hodolee.example.infra.fortune.external.NaverFortuneClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FortuneService {

    private final NaverFortuneClient naverFortuneClient;

    public FortuneService(NaverFortuneClient naverFortuneClient) {
        this.naverFortuneClient = naverFortuneClient;
    }

    @CircuitBreaker(name = "fortuneService", fallbackMethod = "getDefaultFortune")
    public FortuneResponse getFortune(String name, String birthDate) {
        // 네이버 운세 크롤링 반환
        String fortune = naverFortuneClient.getFortune(birthDate);
        return new FortuneResponse(name, birthDate, fortune);
    }

    private FortuneResponse getDefaultFortune(String name, String birthDate, Throwable t) {
        log.info("서킷브레이크 활성화 : {}", t.getMessage());
        String defaultFortune = "하늘을 조심하세요.";
        return new FortuneResponse(name, birthDate, defaultFortune);
    }

}
