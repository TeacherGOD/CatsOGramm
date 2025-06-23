package com.example.catphototg.handlers;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.service.UserService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NameRegistrationHandler implements UpdateHandler {
    private final UserService userService;
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session != null &&
                (session.getState() == UserState.REGISTERING_NAME ||
                        session.getState() == UserState.CHANGING_NAME)) &&
                (message.hasText()||message.isCallback());
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();
        if (message.isCallback() && text.equals(BotConstants.CANCEL_ACTION)) {
            String name = "Аноним";
            userService.updateDisplayName(user,name);
            sessionService.clearSession(telegramId);
            String answer = session.getState() == UserState.CHANGING_NAME ?
                    "✅ Имя успешно изменено на: " + name :
                    personalizeWelcomeMessage(name);
            bot.sendTextWithKeyboard(chatId, answer, bot.createMainMenuKeyboard());
            return;
        }
        if (message.isCallback() && text.equals(BotConstants.CHANGE_NAME_ACTION)) {
            sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
            bot.askForName(chatId);
            return;
        }
        if (!message.isCallback()) {
            if (text != null && text.trim().length() >= 2 && text.trim().length() <= 30) {
                String name = text.trim();
                userService.updateDisplayName(user, name);
                sessionService.clearSession(telegramId);

                String answer = session.getState() == UserState.CHANGING_NAME ?
                        "✅ Имя успешно изменено на: " + name :
                        personalizeWelcomeMessage(name);
                sessionService.getOrCreateSession(user,UserState.MAIN_MENU);
                bot.sendTextWithKeyboard(chatId, answer, bot.createMainMenuKeyboard());
            } else {
                bot.sendTextWithKeyboard(chatId, "Имя должно быть от 2 до 30 символов. Попробуйте еще раз:",
                        bot.createCancelKeyboard());
            }
        }

    }
    private String personalizeWelcomeMessage(String name) {
        return String.format("""
            Привет, %s! Добро пожаловать в мир котиков! 😺
            
            Здесь ты можешь:
            - Добавлять фото своих котиков
            - Смотреть котиков других пользователей
            - Оценивать милых котиков
            
            Начни прямо сейчас!""", name);
    }
}
