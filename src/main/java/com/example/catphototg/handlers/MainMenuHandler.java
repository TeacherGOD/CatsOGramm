package com.example.catphototg.handlers;


import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.MessageFactory;
import com.example.catphototg.service.NavigationService;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.*;

@Component
@RequiredArgsConstructor
public class MainMenuHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final NavigationService navigationService;
    private final MessageFactory messageFactory;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session == null || session.getState() == UserState.MAIN_MENU) &&
                message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        Long chatId = message.chatId();
        switch (message.text()) {
            case ADD_CAT_ACTION:
                sessionService.getOrCreateSession(user, UserState.ADDING_CAT_NAME);
                bot.askForCatName(chatId, user);
                break;

            case VIEW_CATS_ACTION:
                MessageData viewCatsMessage = messageFactory.createTextMessage(
                        "Функция просмотра котиков в разработке",
                        keyboardService.mainMenuKeyboard()
                );
                bot.sendTextWithKeyboard(chatId, viewCatsMessage);
                break;

            case MY_CATS_ACTION:
                MessageData myCatsMessage = messageFactory.createTextMessage(
                        "Функция 'Мои котики' в разработке",
                        keyboardService.mainMenuKeyboard()
                );
                bot.sendTextWithKeyboard(chatId, myCatsMessage);
                break;

            case CHANGE_NAME_ACTION:
                sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
                bot.askForName(chatId);
                break;

            default:
                MessageData defaultMessage = messageFactory.createTextMessage(
                        "Пожалуйста, выберите действие",
                        keyboardService.mainMenuKeyboard()
                );
                bot.sendTextWithKeyboard(chatId, defaultMessage);
        }
    }
}