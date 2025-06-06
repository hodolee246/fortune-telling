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

    private static final String REDIS_VIEW_COUNT_PATTERN = "view_count:*" ;
    private static final String REDIS_VIEW_COUNT_TARGET = "view_count:" ;

    private final RedisTemplate<String, Long> viewCountRedisTemplate;
    private final FortuneRepository fortuneRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000)   // 5분마다 실행
    @Transactional
    public void syncViewCount() {
        Set<String> keys = viewCountRedisTemplate.keys(REDIS_VIEW_COUNT_PATTERN);
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            Long fortuneId = Long.valueOf(key.replace(REDIS_VIEW_COUNT_TARGET, ""));
            Long count = viewCountRedisTemplate.opsForValue().get(key);
            // 조회수 증가
            fortuneRepository.findById(fortuneId).ifPresent(fortune -> fortune.addViewCount(count));
            // redisKey 삭제
            viewCountRedisTemplate.delete(key);
        }
    }
}
