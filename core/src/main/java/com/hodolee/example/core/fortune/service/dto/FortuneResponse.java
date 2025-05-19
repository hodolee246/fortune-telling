package com.hodolee.example.core.fortune.service.dto;

public record FortuneResponse(
        String name,
        String birthDate,
        String fortuneUrl
) {
}
