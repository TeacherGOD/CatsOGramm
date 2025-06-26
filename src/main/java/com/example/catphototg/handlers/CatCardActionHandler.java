package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.BACK_TO_MY_CATS_ACTION;
import static com.example.catphototg.constants.BotConstants.DELETE_CAT_PREFIX;

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