package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    @Transactional
    public UserSession getOrCreateSession(User user, UserState initialState) {
        var session = sessionRepository.findByUserTelegramId(user.getTelegramId())
                .orElseGet(() -> {
                    UserSession newSession = new UserSession();
                    newSession.setUser(user);
                    newSession.setState(initialState);
                    return sessionRepository.save(newSession);
                });
        session.setState(initialState);
        sessionRepository.save(session);
        return session;
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
        Optional<UserSession> session = sessionRepository.findByUserTelegramId(telegramId);

        if (session.isPresent() && isExpired(session.get())) {
            sessionRepository.delete(session.get());
            return Optional.empty();
        }
        return session;
    }

    private boolean isExpired(UserSession session) {
        return session.getCreatedAt().isBefore(LocalDateTime.now().minusHours(2));
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
