package com.hodolee.example.api.fortune.dto;

public record FortuneRequest(
        String name,        // 이름
        String birthDate    // 생년월일
) {}
