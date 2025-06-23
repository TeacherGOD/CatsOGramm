package com.example.catphototg.handlers;


import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@RequiredArgsConstructor
public class AddCatNameHandler implements UpdateHandler {
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, Update update) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_NAME &&
                update.hasMessage() &&
                update.getMessage().hasText();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long telegramId = user.getTelegramId();

        if ("❌ Отмена".equals(text)) {
            sessionService.clearSession(telegramId);
            bot.sendMainMenu(chatId, user);
            return;
        }

        if (text.length() >= 2 && text.length() <= 30) {
            sessionService.updateSession(telegramId, s -> {
                s.setCatName(text);
                s.setState(UserState.ADDING_CAT_PHOTO);
            });
            bot.askForCatPhoto(chatId, user);
        } else {
            bot.sendTextWithKeyBoard(chatId, "Имя котика должно быть от 2 до 30 символов. Попробуйте еще раз:",
                    bot.createCancelKeyboard());
        }
    }
}
