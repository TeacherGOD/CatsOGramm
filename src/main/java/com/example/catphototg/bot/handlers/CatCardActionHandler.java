package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.catservice.service.CatCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.bot.constants.BotConstants.BACK_TO_MY_CATS_ACTION;
import static com.example.catphototg.bot.constants.BotConstants.DELETE_CAT_PREFIX;

@Component
@RequiredArgsConstructor
public class CatCardActionHandler implements UpdateHandler {
    private final CatCardService catCardService;
    private final TelegramFacade bot;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.VIEWING_CAT_DETAILS &&
                message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();

        if (BACK_TO_MY_CATS_ACTION.equals(text)) {
            catCardService.handleBackAction(bot, user, message.chatId());
        } else if (text.equals(DELETE_CAT_PREFIX + session.getViewingCatId())) {
            catCardService.handleDeleteAction(bot, user, message.chatId());
        } else {
            bot.showMainMenu(message.chatId(), user);
        }
    }
}