package com.hodolee.example.domain.fortune.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fortune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String name;
    private String birthDate;
    private String fortuneText;
    private Long viewCount = 0L;
    private LocalDateTime lastViewedAt;

    @Builder
    public Fortune(Long idx, String name, String birthDate, String fortuneText) {
        this.idx = idx;
        this.name = name;
        this.birthDate = birthDate;
        this.fortuneText = fortuneText;
    }

    public void incrementViewCount() {
        this.viewCount++;
        this.lastViewedAt = LocalDateTime.now();
    }

    public void addViewCount(Long count) {
        this.viewCount += count;
        this.lastViewedAt = LocalDateTime.now();
    }
}
