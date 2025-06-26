package com.example.catphototg.bot.handlers;


import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.KeyboardService;
import com.example.catphototg.bot.service.MessageFactory;
import com.example.catphototg.bot.service.NavigationService;
import com.example.catphototg.bot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenuHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final NavigationService navigationService;
    private final MessageFactory messageFactory;
    private final ViewCatsHandler viewCatsHandler;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return (session == null || session.getState() == UserState.MAIN_MENU) &&
                message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        Long chatId = message.chatId();
        switch (message.text()) {
            case ADD_CAT_ACTION:
                sessionService.getOrCreateSession(user, UserState.ADDING_CAT_NAME);
                bot.askForCatName(chatId, user);
                break;

            case VIEW_CATS_ACTION:
                sessionService.getOrCreateSession(user, UserState.VIEWING_RANDOM_CAT);
                viewCatsHandler.showRandomCat(user, chatId);
                break;

            case MY_CATS_ACTION:
                sessionService.getOrCreateSession(
                        user,
                        UserState.BROWSING_MY_CATS
                );
                sessionService.updateSession(user.getTelegramId(),s->s.setCurrentPage(0));
                navigationService.showCatsPage(bot, user, chatId, 0);
                break;

            case CHANGE_NAME_ACTION:
                sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
                bot.askForName(chatId);
                break;

            default:
                MessageData defaultMessage = messageFactory.createTextMessage(
                        DEFAULT_MESSAGE,
                        keyboardService.mainMenuKeyboard()
                );
                bot.sendTextWithKeyboard(chatId, defaultMessage);
        }
    }
}