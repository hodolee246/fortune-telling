package com.hodolee.example.infra.fortune.cache;

public record RedisFortuneResponse(
        String name,        // 이름
        String birthDate,   // 생년월일
        String fortuneText  // 운세 크롤링 텍스트
) {}
