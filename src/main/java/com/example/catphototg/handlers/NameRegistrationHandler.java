package com.example.catphototg.handlers;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.BotOperations;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.*;

@Component
@RequiredArgsConstructor
public class NameRegistrationHandler implements UpdateHandler {
    private final UserService userService;
    private final SessionService sessionService;
    private final BotOperations bot;
    private final KeyboardService keyboardService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session != null &&
                (session.getState() == UserState.REGISTERING_NAME ||
                        session.getState() == UserState.CHANGING_NAME)) &&
                (message.hasText()||message.isCallback());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();
        if (message.isCallback() && text.equals(CANCEL_ACTION)) {
            String name = "Аноним";
            userService.updateDisplayName(user,name);
            sessionService.clearSession(telegramId);
            String answer = session.getState() == UserState.CHANGING_NAME ?
                    String.format(NAME_CHANGED, name) :
                    String.format(WELCOME_MESSAGE, name);
            bot.sendTextWithKeyboard(chatId, answer, keyboardService.mainMenuKeyboard());
            return;
        }
        if (message.isCallback() && text.equals(CHANGE_NAME_ACTION)) {
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
                        String.format(NAME_CHANGED, name) :
                        String.format(WELCOME_MESSAGE, name);
                sessionService.getOrCreateSession(user,UserState.MAIN_MENU);
                bot.sendTextWithKeyboard(chatId, answer, keyboardService.mainMenuKeyboard());
            } else {
                bot.sendTextWithKeyboard(chatId, "Имя должно быть от 2 до 30 символов. Попробуйте еще раз:",
                        keyboardService.cancelKeyboard());
            }
        }

    }
}
