package com.example.catphototg.handlers;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenuHandler implements UpdateHandler {
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session == null || session.getState() == UserState.MAIN_MENU) &&
                message.isCallback();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, TelegramMessage message) {
        Long chatId = message.chatId();
        switch (message.text()) {
            case BotConstants.ADD_CAT_ACTION:
                sessionService.getOrCreateSession(user, UserState.ADDING_CAT_NAME);
                bot.askForCatName(chatId, user);
                break;

            case BotConstants.VIEW_CATS_ACTION:
                bot.sendTextWithKeyboard(chatId, "Функция просмотра котиков в разработке", bot.createMainMenuKeyboard());
                break;

            case BotConstants.MY_CATS_ACTION:
                bot.sendTextWithKeyboard(chatId, "Функция 'Мои котики' в разработке", bot.createMainMenuKeyboard());
                break;

            case BotConstants.CHANGE_NAME_ACTION:
                sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
                bot.askForName(chatId);
                break;

            default:
                bot.sendTextWithKeyboard(chatId, "Пожалуйста, выберите действие", bot.createMainMenuKeyboard());
        }
    }
}