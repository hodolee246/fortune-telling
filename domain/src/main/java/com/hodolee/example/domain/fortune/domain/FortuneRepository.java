package com.hodolee.example.domain.fortune.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneRepository extends JpaRepository<Fortune, Long> {
}
