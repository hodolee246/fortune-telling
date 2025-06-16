package com.hodolee.example.infra.fortune.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FortuneViewProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic}")
    private String topic;

    public void sendViewEvent(Long fortuneId) {
        try {
            ViewEvent viewEvent = new ViewEvent(fortuneId, LocalDateTime.now().toString());
            String eventJson = objectMapper.writeValueAsString(viewEvent);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카프카 메세지 직렬화도중 에러 발생 : ", e);
        }
    }

}
