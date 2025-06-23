package com.example.catphototg.handlers;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.service.UserService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class NameRegistrationHandler implements UpdateHandler {
    private final UserService userService;
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, Update update) {
        return (session != null &&
                (session.getState() == UserState.REGISTERING_NAME ||
                        session.getState() == UserState.CHANGING_NAME)) &&
                update.hasMessage() &&
                update.getMessage().hasText();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long telegramId = user.getTelegramId();

        if ("❌ Отмена".equals(text)) {
            sessionService.clearSession(telegramId);
            if (user.getDisplayName() != null) {
                bot.sendMainMenu(chatId, user);
            } else {
                sessionService.getOrCreateSession(user, UserState.REGISTERING_NAME);
                bot.askForName(chatId);
            }
            return;
        }
        if (text != null && text.trim().length() >= 2 && text.trim().length() <= 30) {
            String name = text.trim();
            userService.updateDisplayName(user, name);
            sessionService.clearSession(telegramId);

            String message = session.getState() == UserState.CHANGING_NAME ?
                    "✅ Имя успешно изменено на: " + name :
                    personalizeWelcomeMessage(name);

            bot.sendTextWithKeyBoard(chatId, message, bot.createMainMenuKeyboard());
        } else {
            bot.sendTextWithKeyBoard(chatId, "Имя должно быть от 2 до 30 символов. Попробуйте еще раз:",
                    bot.createCancelKeyboard());
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
