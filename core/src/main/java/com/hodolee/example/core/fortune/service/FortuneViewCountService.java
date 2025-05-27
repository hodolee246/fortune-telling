package com.hodolee.example.core.fortune.service;

import com.hodolee.example.domain.fortune.domain.FortuneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FortuneViewCountService {

    private final RedisTemplate<String, Long> viewCountRedisTemplate;
    private final FortuneRepository fortuneRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000)   // 5분마다 실행
    @Transactional
    public void syncViewCount() {
        Set<String> keys = viewCountRedisTemplate.keys("view_count:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            Long fortuneId = Long.valueOf(key.replace("view_count:", ""));
            Long count = viewCountRedisTemplate.opsForValue().get(key);
            // 조회수 증가
            fortuneRepository.findById(fortuneId).ifPresent(fortune -> fortune.addViewCount(count));
            // redisKey 삭제
            viewCountRedisTemplate.delete(key);
        }
    }
}
