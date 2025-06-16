package com.hodolee.example.infra.fortune.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneViewConsumer {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Long> redisTemplate;

    @KafkaListener(topics = "fortune-view-topic", groupId = "fortune-view-group")
    public void consume(String message) {
        try {
            ViewEvent event = objectMapper.readValue(message, ViewEvent.class);
            String key = "view_count:" + event.fortuneId();
            redisTemplate.opsForValue().increment(key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카프카 메세지 직렬화도중 에러 발생 : ", e);
        }
    }
}
