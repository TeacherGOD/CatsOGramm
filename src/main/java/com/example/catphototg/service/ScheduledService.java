package com.example.catphototg.service;

import com.example.catphototg.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final SessionRepository sessionRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000) // Каждый час
    @Transactional
    public void cleanExpiredSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        sessionRepository.deleteByCreatedAtBefore(threshold);
    }
}
