package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.NavigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyCatsHandler implements UpdateHandler {
    private final NavigationService navigationService;
    private final TelegramFacade bot;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.BROWSING_MY_CATS &&
                message.isCallback();
    }

    @Override
    public void handle( User user, UserSession session, TelegramMessage message) {

        navigationService.handleMyCatsNavigation(
                bot,
                user,
                message.text(),
                message.chatId()
        );
    }
}
