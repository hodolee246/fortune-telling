package com.hodolee.example.domain.fortune.dto;

public record FortuneDto(
        Long idx,
        String name,
        String birthDate,
        String fortuneText
) {}
