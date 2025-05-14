package com.hodolee.example.domain.fortune;

public record UserInfo(
        String name,        // 이름
        String birthDate,   // 생년월일
        String gender       // 성별
) {}
