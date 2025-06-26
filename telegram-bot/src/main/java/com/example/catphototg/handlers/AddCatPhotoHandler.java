package com.example.catphototg.handlers;


import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatServiceClient;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.MessageFactory;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class AddCatPhotoHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final CatServiceClient catServiceClient;

    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_PHOTO &&
                (message.hasPhoto() ||
                (message.isCallback() && BotConstants.CANCEL_ACTION.equals(message.text())));
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();

        if (message.isCallback() && BotConstants.CANCEL_ACTION.equals(text)) {
            sessionService.clearSession(telegramId);
            bot.showMainMenu(chatId, user);
            return;
        }

        if (message.hasPhoto()) {
            //todo const
            bot.sendText(chatId, new MessageData("📤 Загружаем фото в облако...",null));

            try {

                File tempFile = bot.downloadBotFile(message.photoFileId());

                catServiceClient.uploadFileAsync(tempFile)
                        .thenAccept(storedFilename -> {
                            sessionService.updateSession(user.getTelegramId(), s -> {
                                s.setPhotoFileId(message.photoFileId());
                                s.setFilePath(storedFilename);
                                s.setState(UserState.ADDING_CAT_CONFIRMATION);
                            });

                            MessageData messageData = messageFactory.createCatConfirmationMessage(user, session);
                            bot.sendPhotoWithKeyboard(chatId, message.photoFileId(), messageData);

                            tempFile.delete();
                        })
                        .exceptionally(ex -> {
                            Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                            bot.handleError(chatId, "Ошибка загрузки фото", (Exception) cause, user);
                            return null;
                        });
            } catch (Exception e) {
                sessionService.clearSession(user.getTelegramId());
                bot.handleError(chatId, "Ошибка обработки фото", e, user);
            }

        } else {
            MessageData promptMessage = messageFactory.createTextMessage(
                    "Пожалуйста, отправьте фото котика:",
                    keyboardService.cancelKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, promptMessage);
        }
    }

}
