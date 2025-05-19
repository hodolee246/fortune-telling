package com.hodolee.example.infra.fortune.url;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashidsConfig {

    @Bean
    public Hashids hashids(@Value("${hash.salt}") String salt) {
        return new Hashids(salt, 7);
    }

}
