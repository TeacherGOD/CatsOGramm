package com.example.catphototg.handlers;


import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.BotOperations;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AddCatNameHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final BotOperations bot;
    private final KeyboardService keyboardService;

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
            bot.sendTextWithKeyboard(chatId, "Имя котика должно быть от 2 до 30 символов. Попробуйте еще раз:",
                    keyboardService.cancelKeyboard());
        }
    }
}
