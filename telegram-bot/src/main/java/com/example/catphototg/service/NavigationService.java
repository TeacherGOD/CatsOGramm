package com.example.catphototg.service;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.kafka.CatDto;
import com.example.catphototg.kafka.CatPageRequest;
import com.example.catphototg.kafka.KafkaSender;
import com.example.catphototg.kafka.PagedResponseMy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.catphototg.constants.BotConstants.ASYNC_LOAD_CAT_MSG;
import static com.example.catphototg.constants.BotConstants.MY_CATS_PAGE_MESSAGE;

@Service
@RequiredArgsConstructor
public class NavigationService {
    private final CatServiceClient catServiceClient;
    private final SessionService sessionService;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;
    private final KafkaSender kafkaSender;
    private final TelegramFacade bot;

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
        showCatsPagePrepare(bot, user, chatId, nextPage);
    }

    private void handlePrevPage(TelegramFacade bot, User user, UserSession session, Long chatId) {
        if (session.getCurrentPage() > 0) {
            int prevPage = session.getCurrentPage() - 1;
            sessionService.updateSession(user.getTelegramId(), s -> s.setCurrentPage(prevPage));
            showCatsPagePrepare(bot, user, chatId, prevPage);
        }
    }

    public void showCatsPagePrepare(TelegramFacade bot, User user, Long chatId, int page) {

        bot.sendText(chatId, messageFactory.createTextMessage(ASYNC_LOAD_CAT_MSG, null));
        kafkaSender.sendGetAllMyCat(new CatPageRequest(user.getId(),
                user.getUsername(),
                page,
                9,
                chatId
                ));
    }

    public void showCatsPage(PagedResponseMy<CatDto> pagedResponse){
        String message = String.format(MY_CATS_PAGE_MESSAGE, (pagedResponse.currentPage() + 1));
        Keyboard keyboard = keyboardService.createCatsKeyboard(
                pagedResponse.content(),
                pagedResponse.currentPage(),
                pagedResponse.totalPages()
        );
        MessageData messageData = messageFactory.createTextMessage(message, keyboard);

        bot.sendTextWithKeyboard(pagedResponse.chatId(), messageData);
    }
}
