package com.example.catphototg.handlers;


import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.BotOperations;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.FileStorageService;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class AddCatPhotoHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final FileStorageService fileStorageService;
    private final BotOperations bot;
    private final KeyboardService keyboardService;

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
            try {
                String fileId = message.photoFileId();
                String filePath = bot.getFilePath(fileId);
                File fileData = bot.downloadFile(filePath);

                String storedFilename = fileStorageService.store(fileData);

                UserSession updatedSession = sessionService.updateAndGetSession(telegramId, s -> {
                    s.setPhotoFileId(fileId);
                    s.setFilePath(storedFilename);
                    s.setState(UserState.ADDING_CAT_CONFIRMATION);
                });
                bot.showCatConfirmation(chatId, updatedSession, user);
            } catch (Exception e) {
                bot.handleError(chatId, "Ошибка обработки фото", e,user);
            }

        } else {
            bot.sendTextWithKeyboard(chatId, "Пожалуйста, отправьте фото котика:",
                    keyboardService.cancelKeyboard());
        }
    }

}
