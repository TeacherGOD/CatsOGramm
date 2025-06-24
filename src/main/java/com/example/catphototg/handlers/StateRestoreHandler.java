package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.handlers.interfaces.BotOperations;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.KeyboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.BROWSING_MY_CATS_TEXT;

@Component
@RequiredArgsConstructor
public class StateRestoreHandler implements UpdateHandler {
    private final BotOperations bot;
    private final KeyboardService keyboardService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null && message.text() == null;
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        switch (session.getState()) {
            case ADDING_CAT_NAME:
                bot.askForCatName(message.chatId(), user);
                break;
            case ADDING_CAT_PHOTO:
                bot.askForCatPhoto(message.chatId(), user);
                break;
            case ADDING_CAT_CONFIRMATION:
                bot.showCatConfirmation(message.chatId(), session, user);
                break;
            case REGISTERING_NAME,CHANGING_NAME:
                bot.askForName(message.chatId());
                break;
            case BROWSING_MY_CATS:
                bot.sendTextWithKeyboard(message.chatId(),
                        BROWSING_MY_CATS_TEXT,
                        keyboardService.mainMenuKeyboard());
                break;
            default:
                bot.showMainMenu(message.chatId(), user);
        }
    }
}