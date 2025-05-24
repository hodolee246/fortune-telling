package com.hodolee.example.infra.fortune.redis;

public record RedisFortuneResponse(
        String name,
        String birthDate,
        String fortuneText
) {}
