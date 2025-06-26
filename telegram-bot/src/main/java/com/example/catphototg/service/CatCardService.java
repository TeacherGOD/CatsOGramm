package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class CatCardService {
    private final CatServiceClient catServiceClient;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final SessionService sessionService;
    private final NavigationService navigationService;

    @Value("${cat.service.files-url}")
    private String filesBaseUrl;

    public void showCatCard(TelegramFacade bot, User user, Long catId, int currentPage, Long chatId) {
        //todo const
        bot.sendText(chatId, messageFactory.createTextMessage("⌛ Загружаем информацию о котике...", null));
        catServiceClient.getCatByChatIdAsync(catId, user.getUsername())
            .thenCompose(catDto -> {
                sessionService.updateSession(user.getTelegramId(), session -> {
                    session.setViewingCatId(catId);
                    session.setCurrentPage(currentPage);
                    session.setState(UserState.VIEWING_CAT_DETAILS);
                });

                String caption = "🐱 Имя: " + catDto.name();
                Keyboard keyboard = keyboardService.createCatDetailsKeyboard(catId);
                MessageData messageData = messageFactory.createTextMessage(caption, keyboard);

                return catServiceClient.getFileAsync(catDto.filePath())
                        .thenAccept(resource -> {
                            try {
                                File tempFile = File.createTempFile("cat", ".jpg");
                                try (InputStream in = resource.getInputStream();
                                     OutputStream out = new FileOutputStream(tempFile)) {
                                    in.transferTo(out);
                                }
                                bot.sendPhotoFromFile(chatId, tempFile, messageData);
                                tempFile.delete();
                            } catch (IOException e) {
                                bot.handleError(chatId, "Ошибка обработки файла", e, user);
                            }
                        });
            })
            .exceptionally(ex -> {
                Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                bot.handleError(chatId, "Ошибка загрузки котика", (Exception) cause, user);
                return null;
            });
    }

    public void handleBackAction(TelegramFacade bot, User user, Long chatId) {
        UserSession session = sessionService.findByUserTelegramId(user.getTelegramId())
                .orElseThrow();

        navigationService.showCatsPage(bot, user, chatId, session.getCurrentPage());
        sessionService.updateSession(user.getTelegramId(), s ->
                s.setState(UserState.BROWSING_MY_CATS));
    }

    public void handleDeleteAction(TelegramFacade bot, User user, Long chatId) {
        UserSession session = sessionService.findByUserTelegramId(user.getTelegramId())
                .orElseThrow();

        if (session.getViewingCatId() == null) {
            bot.handleError(chatId, "Не выбран котик для удаления", null, user);
            return;
        }
        Long catIdToDelete = session.getViewingCatId();
        int currentPage = session.getCurrentPage();

        bot.sendText(chatId, new MessageData("Начинаем удаление котика...",null));

        catServiceClient.deleteCatAsync(catIdToDelete, user.getId())
                .thenAccept(deleted -> {
                    if (deleted) {
                        updateUIAfterDeletion(bot, user, chatId, currentPage);
                    } else {
                        bot.handleError(chatId, "Не удалось удалить котика", null, user);
                    }
                })
                .exceptionally(ex -> {
                    bot.handleError(chatId, "Ошибка при удалении котика", (Exception) ex, user);
                    return null;
                });
    }

    private void updateUIAfterDeletion(TelegramFacade bot, User user, Long chatId,
                                       int currentPage) {

        catServiceClient.getCatsCountAsync(user.getId())
                .thenAccept(totalPages -> {
                    int newPage = calculateNewPage(currentPage, totalPages);

                    sessionService.updateSession(user.getTelegramId(), s -> {
                        s.setViewingCatId(null);
                        s.setCurrentPage(newPage);
                        s.setState(UserState.BROWSING_MY_CATS);
                    });

                    //todo const но уже?
                    String successMsg = "Котик успешно удален ✅";
                    bot.sendText(chatId, messageFactory.createTextMessage(successMsg, null));

                    navigationService.showCatsPage(bot, user, chatId, newPage);
                });
    }
    private int calculateNewPage(int currentPage, int totalPages) {
        if (currentPage >= totalPages && totalPages > 0) {
            return totalPages - 1;
        }
        return currentPage;
    }
}