package com.example.catphototg.handlers;


import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.FileStorageService;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.MessageFactory;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.example.catphototg.constants.BotConstants.ADD_CAT_PROMPT_MESSAGE;
import static com.example.catphototg.constants.BotConstants.CANCEL_ACTION;

@Component
@RequiredArgsConstructor
public class AddCatPhotoHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final FileStorageService fileStorageService;

    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_PHOTO &&
                (message.hasPhoto() ||
                (message.isCallback() && CANCEL_ACTION.equals(message.text())));
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();

        if (message.isCallback() && CANCEL_ACTION.equals(text)) {
            sessionService.clearSession(telegramId);
            bot.showMainMenu(chatId, user);
            return;
        }

        if (message.hasPhoto()) {
            try {
                String filePath = bot.getFilePath(message.photoFileId());
                File fileData = bot.downloadBotFile(filePath);
                String storedFilename = fileStorageService.store(fileData);

                UserSession updatedSession = sessionService.updateAndGetSession(telegramId, s -> {
                    s.setPhotoFileId(message.photoFileId());
                    s.setFilePath(storedFilename);
                    s.setPhotoFileId(message.photoFileId());
                    s.setState(UserState.ADDING_CAT_CONFIRMATION);
                });
                MessageData messageData = messageFactory.createCatConfirmationMessage(user, updatedSession);
                bot.sendPhotoWithKeyboard(chatId, updatedSession.getPhotoFileId(), messageData);
            } catch (Exception e) {
                sessionService.clearSession(user.getTelegramId());
                bot.handleError(chatId, "Ошибка обработки фото", e,user);
            }

        } else {
            MessageData promptMessage = messageFactory.createTextMessage(
                    ADD_CAT_PROMPT_MESSAGE,
                    keyboardService.cancelKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, promptMessage);
        }
    }

}
