package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.NavigationService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyCatsHandler implements UpdateHandler {
    private final NavigationService navigationService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.BROWSING_MY_CATS &&
                message.isCallback();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, TelegramMessage message) {
        navigationService.handleMyCatsNavigation(
                bot,
                user,
                message.text(),
                message.chatId()
        );
    }
}
