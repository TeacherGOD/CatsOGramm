package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.MessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.BROWSING_MY_CATS_TEXT;

@Component
@RequiredArgsConstructor
public class StateRestoreHandler implements UpdateHandler {
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;

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
                MessageData browsingMessage = messageFactory.createTextMessage(
                        BROWSING_MY_CATS_TEXT,
                        keyboardService.mainMenuKeyboard()
                );
                bot.sendTextWithKeyboard(message.chatId(), browsingMessage);
                break;
            default:
                bot.showMainMenu(message.chatId(), user);
        }
    }
}