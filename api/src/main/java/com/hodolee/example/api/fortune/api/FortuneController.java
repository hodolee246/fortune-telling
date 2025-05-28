package com.hodolee.example.api.fortune.api;

import com.hodolee.example.api.fortune.dto.FortuneRequest;
import com.hodolee.example.core.fortune.service.FortuneService;
import com.hodolee.example.core.fortune.service.dto.FortuneResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/fortune")
public class FortuneController {

    private final FortuneService fortuneService;

    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    @PostMapping
    public ResponseEntity<Object> getFortuneUrl(@RequestBody FortuneRequest request) {
        String fortuneUrl = fortuneService.getFortuneUrl(request.name(), request.birthDate());
        return ResponseEntity.created(URI.create(fortuneUrl)).build();
    }

    @GetMapping("{encryptIdx}")
    public ResponseEntity<FortuneResponse> getFortune(@PathVariable("encryptIdx") String encryptIdx) {
        return ResponseEntity.ok(fortuneService.getFortune(encryptIdx));
    }

}
