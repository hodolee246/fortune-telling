package com.hodolee.example.domain.fortune;

import java.util.UUID;

public record FortuneResult(
        UUID uuid,
        String name,
        String job,     // 직업운
        String love,    // 연애운
        String life     // 일상운
) {}
