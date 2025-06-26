package com.example.catphototg.bot.repository;

import com.example.catphototg.bot.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<UserSession, Long> {

    void deleteByUserId(Long userId);

    void deleteByCreatedAtBefore(LocalDateTime threshold);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.user.telegramId = :telegramId")
    void deleteByUserTelegramId(@Param("telegramId") Long telegramId);

    Optional<UserSession> findByUserTelegramId(Long telegramId);
}
