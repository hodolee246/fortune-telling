package com.hodolee.example.api.fortune;

import com.hodolee.example.core.fortune.FortuneService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FortuneController {

    private final FortuneService fortService;

    public FortuneController(FortuneService fortService) {
        this.fortService = fortService;
    }

}
