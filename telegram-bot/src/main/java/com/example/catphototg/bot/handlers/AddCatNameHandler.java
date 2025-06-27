package com.example.catphototg.bot.handlers;


import com.example.catphototg.bot.constants.BotConstants;
import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.KeyboardService;
import com.example.catphototg.bot.service.MessageFactory;
import com.example.catphototg.bot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AddCatNameHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final KeyboardService keyboardService;
    private final MessageFactory messageFactory;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_NAME &&
                (message.isCallback() ||
                message.hasText());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        Long chatId = message.chatId();
        String text = message.text();

        if (message.isCallback()&&BotConstants.CANCEL_ACTION.equals(text)) {
            sessionService.clearSession(user.getTelegramId());
            bot.showMainMenu(chatId, user);
            return;
        }

        if (text.length() >= 2 && text.length() <= 30) {
            sessionService.updateSession(message.userId(), s -> {
                s.setCatName(text);
                s.setState(UserState.ADDING_CAT_PHOTO);
            });
            bot.askForCatPhoto(chatId, user);
        } else {
            MessageData errorMessage = messageFactory.createTextMessage(
                    CAT_NAME_ERROR_MESSAGE,
                    keyboardService.cancelKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, errorMessage);
        }
    }
}
