package com.example.catphototg.service;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationService {
    private final CatService catService;
    private final SessionService sessionService;
    private final BotMessageService botMessageService;

    public void handleMyCatsNavigation(CatBot bot, User user, String callbackData, Long chatId) {
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
                botMessageService.sendMainMenu(bot, chatId, user);
                sessionService.clearSession(user.getTelegramId());
                break;
            default:
                break;
        }
    }

    private void handleNextPage(CatBot bot, User user, UserSession session, Long chatId) {
        int nextPage = session.getCurrentPage() + 1;
        sessionService.updateSession(user.getTelegramId(), s -> s.setCurrentPage(nextPage));
        showCatsPage(bot, user, chatId, nextPage);
    }

    private void handlePrevPage(CatBot bot, User user, UserSession session, Long chatId) {
        if (session.getCurrentPage() > 0) {
            int prevPage = session.getCurrentPage() - 1;
            sessionService.updateSession(user.getTelegramId(), s -> s.setCurrentPage(prevPage));
            showCatsPage(bot, user, chatId, prevPage);
        }
    }

    public void showCatsPage(CatBot bot, User user, Long chatId, int page) {
        Page<Cat> catPage = catService.getCatsByAuthor(user, page, 9);
        botMessageService.sendCatsPage(bot, chatId, catPage, page);
    }
}
