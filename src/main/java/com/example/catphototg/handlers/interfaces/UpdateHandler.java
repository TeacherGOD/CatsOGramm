package com.example.catphototg.handlers.interfaces;


import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;

public interface UpdateHandler {
    boolean canHandle(User user, UserSession session, TelegramMessage message);
    void handle(User user, UserSession session, TelegramMessage message);
}