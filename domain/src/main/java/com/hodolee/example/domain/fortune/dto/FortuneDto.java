package com.hodolee.example.domain.fortune.dto;

public record FortuneDto(
        Long idx,           // 고유키
        String name,        // 이름
        String birthDate,   // 생년월일
        String fortuneText  // 운세 크롤링 텍스트
) {}
