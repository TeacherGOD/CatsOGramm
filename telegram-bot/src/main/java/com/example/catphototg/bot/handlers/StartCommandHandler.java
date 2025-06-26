package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return message.hasText() && "/start".equals(message.text());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        bot.showMainMenu(message.chatId(), user);
        sessionService.getOrCreateSession(user, UserState.MAIN_MENU);
    }
}