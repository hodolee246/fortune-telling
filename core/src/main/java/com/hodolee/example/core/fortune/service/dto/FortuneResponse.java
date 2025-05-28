package com.hodolee.example.core.fortune.service.dto;

public record FortuneResponse(
        String name,        // 이름
        String birthDate,   // 생년월일
        String fortuneText  // 운세 크롤링 텍스트
) {
}