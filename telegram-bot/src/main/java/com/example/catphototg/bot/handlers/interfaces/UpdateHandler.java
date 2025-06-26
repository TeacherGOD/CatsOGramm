package com.example.catphototg.bot.handlers.interfaces;


import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;

public interface UpdateHandler {
    boolean canHandle(User user, UserSession session, TelegramMessage message);
    void handle(User user, UserSession session, TelegramMessage message);
}