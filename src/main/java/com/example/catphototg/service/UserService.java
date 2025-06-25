package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Transactional
    public User createUser(Long telegramId, String username) {
        User newUser = new User();
        newUser.setTelegramId(telegramId);
        newUser.setUsername(username);
        return userRepository.save(newUser);
    }


    @Transactional
    public void updateDisplayName(User user, String displayName) {
        user.setDisplayName(displayName);
        userRepository.save(user);
    }
}