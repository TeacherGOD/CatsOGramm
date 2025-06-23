package com.example.catphototg.handlers.interfaces;


import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.tgbot.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    boolean canHandle(User user, UserSession session, Update update);
    void handle(CatBot bot, User user, UserSession session, Update update);
}