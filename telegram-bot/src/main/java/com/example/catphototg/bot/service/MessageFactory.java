package com.example.catphototg.bot.service;

import com.example.catphototg.bot.constants.BotConstants;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.ui.Keyboard;
import com.example.catphototg.bot.entity.ui.MessageData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.catphototg.bot.constants.BotConstants.*;


@Service
@RequiredArgsConstructor
public class MessageFactory {
    private final KeyboardService keyboardService;

    public MessageData createMainMenuMessage(User user) {
        String text = user.getDisplayName() + ", выбери действие:";
        return new MessageData(text, keyboardService.mainMenuKeyboard());
    }

    public MessageData createTextMessage(String text, Keyboard keyboard) {
        return new MessageData(text, keyboard);
    }

    public MessageData createNameRegistrationPrompt() {
        return new MessageData(NAME_REGISTRATION_PROMPT, keyboardService.cancelKeyboard());
    }

    public MessageData createErrorMessage(User user, String errorDetails) {
        String userName = (user != null && user.getDisplayName() != null) ?
                user.getDisplayName() : "Пользователь";
        String text = "😿 Упс, " + userName + ", произошла ошибка: " + errorDetails;
        return new MessageData(text, keyboardService.mainMenuKeyboard());
    }

    public MessageData createCatNamePrompt(User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_NAME_PROMPT;
        return new MessageData(message, keyboardService.cancelKeyboard());
    }

    public MessageData createCatPhotoPrompt(User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_PHOTO_PROMPT;
        return new MessageData(message, keyboardService.cancelKeyboard());
    }

    public MessageData createCatConfirmationMessage(User user, UserSession session) {
        String caption = String.format(
                CAT_CONFIRMATION_PROMPT,user.getDisplayName(),
                session.getCatName(),user.getUsername());
        return new MessageData(caption, keyboardService.confirmationKeyboard());
    }
}
