package com.example.catphototg.service;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.catphototg.constants.BotConstants.*;


@Service
@RequiredArgsConstructor
public class CatCardService {
    private final CatService catService;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;
    private final SessionService sessionService;
    private final NavigationService navigationService;

    public void showCatCard(TelegramFacade bot, User user, Long catId, int currentPage, Long chatId) {
        Cat cat;
        try {
            cat = catService.getCatById(catId);
        } catch (CatNotFoundException e) {
            bot.handleError(chatId, NO_CAT_FUNDED, e, user);
            return;
        }

        if (!cat.getAuthor().getId().equals(user.getId())) {
            bot.handleError(chatId, "Котик не найден или недоступен", null, user);
            return;
        }

        sessionService.updateSession(user.getTelegramId(), session -> {
            session.setViewingCatId(catId);
            session.setCurrentPage(currentPage);
            session.setState(UserState.VIEWING_CAT_DETAILS);
        });

        String caption = "🐱 Имя: " + cat.getName();
        Keyboard keyboard = keyboardService.createCatDetailsKeyboard(catId);
        MessageData messageData = messageFactory.createTextMessage(caption, keyboard);

        bot.sendPhotoFromFile(chatId, cat.getFilePath(), messageData);
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

        boolean deleted = catService.deleteCatById(session.getViewingCatId(), user);
        if (!deleted) {
            bot.handleError(chatId, "Не удалось удалить котика", null, user);
            return;
        }

        int currentPage = session.getCurrentPage();
        int totalPages = catService.getCatsByAuthor(user, 0, 9).getTotalPages();

        if (currentPage >= totalPages && totalPages > 0) {
            currentPage = totalPages - 1;
        }

        bot.sendText(chatId,messageFactory.createTextMessage(CAT_SUCCESS_DELETE_MESSAGE,null));

        int finalCurrentPage = currentPage;
        sessionService.updateSession(user.getTelegramId(), s -> {
            s.setViewingCatId(null);
            s.setCurrentPage(finalCurrentPage);
            s.setState(UserState.BROWSING_MY_CATS);
        });
        navigationService.showCatsPage(bot,user,chatId,currentPage);
    }
}