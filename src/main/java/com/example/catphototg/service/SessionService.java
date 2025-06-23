package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    @Transactional
    public UserSession getOrCreateSession(User user, UserState initialState) {
        return sessionRepository.findByUserTelegramId(user.getTelegramId())
                .orElseGet(() -> {
                    UserSession session = new UserSession();
                    session.setUser(user);
                    session.setState(initialState);
                    return sessionRepository.save(session);
                });
    }
    @Transactional
    public void updateSession(Long telegramId, Consumer<UserSession> updater) {
        sessionRepository.findByUserTelegramId(telegramId).ifPresent(session -> {
            updater.accept(session);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void clearSession(Long telegramId) {
        sessionRepository.deleteByUserTelegramId(telegramId);
    }

    public Optional<UserSession> findByUserTelegramId(Long telegramId) {
        return sessionRepository.findByUserTelegramId(telegramId);
    }

    @Transactional
    public UserSession updateAndGetSession(Long telegramId, Consumer<UserSession> updater) {
        return sessionRepository.findByUserTelegramId(telegramId)
                .map(session -> {
                    updater.accept(session);
                    return sessionRepository.save(session);
                })
                .orElseThrow(() -> new IllegalStateException("Session not found"));
    }
}
