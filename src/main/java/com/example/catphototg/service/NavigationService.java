package com.example.catphototg.service;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationService {
    private final CatService catService;
    private final SessionService sessionService;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;

    public void handleMyCatsNavigation(TelegramFacade bot, User user, String callbackData, Long chatId) {
        UserSession session = sessionService.findByUserTelegramId(user.getTelegramId())
                .orElseThrow();

        switch (callbackData) {
            case BotConstants.NEXT_PAGE_ACTION:
                handleNextPage(bot, user, session, chatId);
                break;
            case BotConstants.PREV_PAGE_ACTION:
                handlePrevPage(bot, user, session, chatId);
                break;
            case BotConstants.BACK_TO_MENU_ACTION:
                bot.showMainMenu(chatId, user);
                sessionService.clearSession(user.getTelegramId());
                break;
            default:
                break;
        }
    }

    private void handleNextPage(TelegramFacade bot, User user, UserSession session, Long chatId) {
        int nextPage = session.getCurrentPage() + 1;
        sessionService.updateSession(user.getTelegramId(), s -> s.setCurrentPage(nextPage));
        showCatsPage(bot, user, chatId, nextPage);
    }

    private void handlePrevPage(TelegramFacade bot, User user, UserSession session, Long chatId) {
        if (session.getCurrentPage() > 0) {
            int prevPage = session.getCurrentPage() - 1;
            sessionService.updateSession(user.getTelegramId(), s -> s.setCurrentPage(prevPage));
            showCatsPage(bot, user, chatId, prevPage);
        }
    }

    public void showCatsPage(TelegramFacade bot, User user, Long chatId, int page) {
        Page<Cat> catPage = catService.getCatsByAuthor(user, page, 9);
        String message = "Ваши котики (страница " + (page + 1) + "):";
        Keyboard keyboard = keyboardService.createCatsKeyboard(catPage, page);
        MessageData messageData = messageFactory.createTextMessage(message, keyboard);

        bot.sendTextWithKeyboard(chatId, messageData);
    }
}
