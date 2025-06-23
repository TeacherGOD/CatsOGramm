package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;


    @Transactional
    public User findOrCreateUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Long telegramId = telegramUser.getId();

        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramId(telegramId);
                    newUser.setUsername(telegramUser.getUserName());
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public void updateDisplayName(User user, String displayName) {
        user.setDisplayName(displayName);
        userRepository.save(user);
    }
}