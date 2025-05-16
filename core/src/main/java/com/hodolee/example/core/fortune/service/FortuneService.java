package com.hodolee.example.core.fortune.service;

import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import org.springframework.stereotype.Service;

@Service
public class FortuneService {

    public FortuneResponse getFortune(String name, String birthDate) {
        // TODO 외부 API 및 특정 데이터 기반 운세 반환

        // TODO 서킷브레이크 생년월일 기반 기본 운세 반환
        return null;
    }

}
