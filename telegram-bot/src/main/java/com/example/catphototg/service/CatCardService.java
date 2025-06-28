package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.kafka.CatDetailsKafka;
import com.example.catphototg.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.catphototg.constants.BotConstants.*;

@Service
@RequiredArgsConstructor
public class CatCardService {
    private final CatServiceClient catServiceClient;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final SessionService sessionService;
    private final NavigationService navigationService;
    private final TelegramFacade bot;
    private final KafkaSender kafkaSender;

    @Value("${cat.service.files-url}")
    private String filesBaseUrl;
    private final FileStorageService fileStorageService;

    public void showCatCardPrepare(Long telegramId, Long catId,  Long chatId,Long userId) {

        bot.sendText(chatId, messageFactory.createTextMessage(ASYNC_LOAD_CAT_INFO_MSG, null));
        kafkaSender.sendCatDetails(new CatDetailsKafka(
                telegramId,
                catId,
                null,
                null,
                chatId,
                userId
        ));

    }

    public void showCatCard(CatDetailsKafka catDetailsKafka){//tgId, catId, catName, filepath, chatId
        sessionService.updateSession(catDetailsKafka.telegramId(), session -> {
            session.setViewingCatId(catDetailsKafka.catId());
            session.setState(UserState.VIEWING_CAT_DETAILS);
        });

        String caption = String.format(CAT_NAME, catDetailsKafka.catName());
        Keyboard keyboard = keyboardService.createCatDetailsKeyboard(catDetailsKafka.catId());
        MessageData messageData = messageFactory.createTextMessage(caption, keyboard);

        try {
            var photo = fileStorageService.load(catDetailsKafka.filePath());
            bot.sendPhotoFromFile(catDetailsKafka.catId(), photo.toFile(), messageData);
            fileStorageService.delete(photo.getFileName().toString());
        } catch (Exception e) {
            bot.handleError(catDetailsKafka.catId(), "Ошибка получения файла", e, null);
        }
    }

    public void handleBackAction(TelegramFacade bot, User user, Long chatId) {
        UserSession session = sessionService.findByUserTelegramId(user.getTelegramId())
                .orElseThrow();

        navigationService.showCatsPagePrepare(bot, user, chatId, session.getCurrentPage());
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
        catServiceClient.deleteCatAsync(catIdToDelete, user.getId());

        updateUIAfterDeletionPrepare(bot, user, chatId, currentPage);


    }

    public void updateUIAfterDeletionPrepare(TelegramFacade bot, User user, Long chatId,
                                              int currentPage) {
        kafkaSender.sendCountCatsAsk(user.getId());

    }

    public void updateUIAfterDeletion(Long userId, Long chatId, Long telegramId, String username,
                                              int countCats) {
        try {
            int totalPages = countCats/9;
            var session = sessionService.getSession(userId);

            int newPage = calculateNewPage(session.getCurrentPage(), totalPages);
            var fakeUser=new User();
            fakeUser.setTelegramId(telegramId);
            fakeUser.setId(userId);
            fakeUser.setUsername(username);
            sessionService.updateSession(fakeUser.getTelegramId(), s -> {
                s.setViewingCatId(null);
                s.setCurrentPage(newPage);
                s.setState(UserState.BROWSING_MY_CATS);
            });

            bot.sendText(chatId, messageFactory.createTextMessage(CAT_SUCCESS_DELETE_MESSAGE, null));

            navigationService.showCatsPagePrepare(bot, fakeUser, chatId, newPage);
        } catch (Exception ex) {
            bot.handleError(chatId, "Ошибка получения количества котиков", ex, null);
        }

    }


    private int calculateNewPage(int currentPage, int totalPages) {
        if (currentPage >= totalPages && totalPages > 0) {
            return totalPages - 1;
        }
        return currentPage;
    }
}