package com.example.catphototg.handlers;


import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.FileStorageService;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddCatPhotoHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final FileStorageService fileStorageService;

    @Override
    public boolean canHandle(User user, UserSession session, Update update) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_PHOTO &&
                update.hasMessage();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Long telegramId = user.getTelegramId();

        if (message.hasText() && "❌ Отмена".equals(message.getText())) {
            sessionService.clearSession(telegramId);
            bot.sendMainMenu(chatId, user);
            return;
        }

        if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            PhotoSize bestPhoto = photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null);

            if (bestPhoto != null && bestPhoto.getFileId() != null && !bestPhoto.getFileId().isEmpty()) {
                try {
                    String fileId = bestPhoto.getFileId();
                    String filePath = bot.getFilePath(fileId);
                    File fileData = bot.downloadFile(filePath);

                    String storedFilename = fileStorageService.store(fileData);

                    UserSession updatedSession = sessionService.updateAndGetSession(telegramId, s -> {
                        s.setPhotoFileId(bestPhoto.getFileId());
                        s.setFilePath(storedFilename);
                        s.setState(UserState.ADDING_CAT_CONFIRMATION);
                    });
                    bot.showCatConfirmation(chatId, updatedSession, user);
                } catch (Exception e) {
                    bot.handleError(chatId, "Ошибка обработки фото", e,user);
                }
            }
        } else {
            bot.sendTextWithKeyBoard(chatId, "Пожалуйста, отправьте фото котика:",
                    bot.createCancelKeyboard());
        }
    }
}
