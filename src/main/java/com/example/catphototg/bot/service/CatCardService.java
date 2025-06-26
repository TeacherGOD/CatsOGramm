package com.example.catphototg.bot.service;

import com.example.catphototg.catservice.entity.Cat;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.Keyboard;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.catservice.exceptions.CatNotFoundException;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.catservice.service.CatService;
import com.example.catphototg.catservice.service.CatServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.catphototg.bot.constants.BotConstants.NO_CAT_FUNDED;

@Service
@RequiredArgsConstructor
public class CatCardService {
    private final CatServiceClient catServiceClient;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final SessionService sessionService;
    private final NavigationService navigationService;

    public void showCatCard(TelegramFacade bot, User user, Long catId, int currentPage, Long chatId) {
        bot.sendText(chatId, messageFactory.createTextMessage("⌛ Загружаем информацию о котике...", null));
        catServiceClient.getCatByIdAsync(catId)
                .thenAccept(catDto -> {
                    sessionService.updateSession(user.getTelegramId(), session -> {
                        session.setViewingCatId(catId);
                        session.setCurrentPage(currentPage);
                        session.setState(UserState.VIEWING_CAT_DETAILS);
                    });

                    String caption = "🐱 Имя: " + catDto.name();
                    Keyboard keyboard = keyboardService.createCatDetailsKeyboard(catId, currentPage);
                    MessageData messageData = messageFactory.createTextMessage(caption, keyboard);

                    if (catDto.filePath() != null && !catDto.filePath().isEmpty()) {
                        bot.sendPhotoFromFile(chatId, catDto.filePath(), messageData);
                    } else {
                        String noPhotoMsg = "У этого котика нет фото 😿\n\n" + caption;
                        bot.sendTextWithKeyboard(chatId,
                                messageFactory.createTextMessage(noPhotoMsg, keyboard));
                    }
                })
                .exceptionally(ex -> {
                    bot.handleError(chatId, "Ошибка загрузки котика", ex, user);
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