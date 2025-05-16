package com.hodolee.example.api.fortune.api;

import com.hodolee.example.api.fortune.dto.FortuneRequest;
import com.hodolee.example.api.fortune.response.FortuneResponse;
import com.hodolee.example.core.fortune.service.FortuneService;
import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/fortune")
public class FortuneController {

    private final FortuneService fortuneService;

    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    @PostMapping
    public ResponseEntity<FortuneResponse> getFortune(@RequestBody FortuneRequest request) {
        return ResponseEntity.ok().body(fortuneService.getFortune(request.name(), request.birthDate()));
    }

}
