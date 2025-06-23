package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements UpdateHandler {
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return message.hasText() && "/start".equals(message.text());
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, TelegramMessage message) {
        bot.showMainMenu(message.chatId(), user);
        sessionService.getOrCreateSession(user, UserState.MAIN_MENU);
    }
}