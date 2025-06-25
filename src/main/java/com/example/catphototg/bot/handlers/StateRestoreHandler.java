package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.service.SessionService;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.catservice.service.CatCardService;
import com.example.catphototg.bot.service.MessageFactory;
import com.example.catphototg.bot.service.NavigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateRestoreHandler implements UpdateHandler {
    private final TelegramFacade bot;
    private final NavigationService navigationService;
    private final MessageFactory messageFactory;
    private final CatCardService catCardService;
    private final ViewCatsHandler viewCatsHandler;
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() != UserState.MAIN_MENU &&
                !message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        switch (session.getState()) {
            case ADDING_CAT_NAME:
                bot.askForCatName(message.chatId(), user);
                break;
            case ADDING_CAT_PHOTO:
                bot.askForCatPhoto(message.chatId(), user);
                break;
            case ADDING_CAT_CONFIRMATION:
                MessageData messageData = messageFactory.createCatConfirmationMessage(user, session);
                bot.sendPhotoWithKeyboard(message.chatId(), session.getPhotoFileId(), messageData);
                break;
            case REGISTERING_NAME,CHANGING_NAME:
                bot.askForName(message.chatId());
                break;
            case BROWSING_MY_CATS:
                int currentPage = session.getCurrentPage();
                navigationService.showCatsPage(bot, user, message.chatId(), currentPage);
                break;
            case VIEWING_CAT_DETAILS:
                catCardService.showCatCard(
                        bot,
                        user,
                        session.getViewingCatId(),
                        message.chatId()
                );
                sessionService.updateSession(user.getTelegramId(), s -> {
                    s.setViewingCatId(session.getViewingCatId());
                    s.setCurrentPage(session.getCurrentPage());
                    s.setState(UserState.VIEWING_CAT_DETAILS);
                });
                break;
            case VIEWING_RANDOM_CAT:
                if (session.getViewingCatId() != null) {
                    viewCatsHandler.showRandomCat(user, message.chatId());
                } else {
                    viewCatsHandler.handle(user, session, message);
                }
                break;
            default:
                bot.showMainMenu(message.chatId(), user);
        }
    }
}