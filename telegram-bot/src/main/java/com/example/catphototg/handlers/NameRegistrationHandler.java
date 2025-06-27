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
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session != null &&
                (session.getState() == UserState.REGISTERING_NAME ||
                        session.getState() == UserState.CHANGING_NAME)) &&
                (message.hasText()||message.isCallback());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        if (message.isCallback()) {
            handleCallback(user, session, message);
        } else {
            handleTextMessage(user, session, message);
        }
    }

    private void handleCallback(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();

        if (CANCEL_ACTION.equals(text)) {
            handleCancelAction(user, session, chatId);
        } else if (CHANGE_NAME_ACTION.equals(text)) {
            handleChangeNameAction(user, chatId);
        }
    }

    private void handleCancelAction(User user, UserSession session, Long chatId) {
        Long telegramId = user.getTelegramId();
        String name = "Аноним";

        if (session.getState() == UserState.REGISTERING_NAME) {
            processNameUpdate(user, name, chatId, session.getState());
        } else {
            sessionService.clearSession(telegramId);
            bot.showMainMenu(chatId, user);
        }
    }

    private void handleChangeNameAction(User user, Long chatId) {
        sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
        bot.askForName(chatId);
    }

    private void handleTextMessage(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();

        if (isValidName(text)) {
            processNameUpdate(user, text.trim(), chatId, session.getState());
        } else {
            sendNameValidationError(chatId);
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 30;
    }

    private void processNameUpdate(User user, String name, Long chatId, UserState state) {
        Long telegramId = user.getTelegramId();
        userService.updateDisplayName(user, name);
        sessionService.clearSession(telegramId);
        sessionService.getOrCreateSession(user, UserState.MAIN_MENU);

        String answer = state == UserState.CHANGING_NAME ?
                String.format(NAME_CHANGED, name) :
                String.format(WELCOME_MESSAGE, name);

        MessageData messageData = messageFactory.createTextMessage(
                answer,
                keyboardService.mainMenuKeyboard()
        );
        bot.sendTextWithKeyboard(chatId, messageData);
    }

    private void sendNameValidationError(Long chatId) {
        MessageData errorMessage = messageFactory.createTextMessage(
                NAME_ERROR_MESSAGE,
                keyboardService.cancelKeyboard()
        );
        bot.sendTextWithKeyboard(chatId, errorMessage);
    }
}
