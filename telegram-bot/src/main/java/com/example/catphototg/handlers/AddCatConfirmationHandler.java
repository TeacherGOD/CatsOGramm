package com.example.catphototg.handlers;


import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.*;
import com.example.common.dto.CatCreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.*;

@Component
@RequiredArgsConstructor
public class AddCatConfirmationHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;
    private final CatServiceClient catServiceClient;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_CONFIRMATION &&
                message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();

        if (CONFIRM_CAT_ACTION.equals(text)) {
            CatCreationDto dto = new CatCreationDto(
                    session.getCatName(),
                    session.getFilePath(),
                    user.getId(),
                    user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()
            );
            catServiceClient.addCatAsync(dto)
                    .thenAccept(cat -> {
                        sessionService.clearSession(telegramId);

                        String successText = String.format(CAT_SUCCESS_ADD_MESSAGE,cat.name());
                        MessageData successMessage = messageFactory.createTextMessage(
                                successText,
                                keyboardService.mainMenuKeyboard()
                        );
                        bot.sendTextWithKeyboard(chatId, successMessage);
                    })
                    .exceptionally(ex -> {
                        bot.handleError(chatId, "Ошибка при добавлении котика", (Exception) ex, user);
                        return null;
                    });

            bot.sendText(chatId, messageFactory.createTextMessage(ASYNC_CAT_ADD_MESSAGE,null));
        }
        else if (CANCEL_CAT_ACTION.equals(text)) {
            sessionService.clearSession(telegramId);
            MessageData cancelMessage = messageFactory.createTextMessage(
                    CANCEL_ADD_CAT_MESSAGE,
                    keyboardService.mainMenuKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, cancelMessage);
        }
        else {
            MessageData messageData = messageFactory.createCatConfirmationMessage(user, session);
            bot.sendPhotoWithKeyboard(chatId, session.getPhotoFileId(), messageData);
        }
    }
}