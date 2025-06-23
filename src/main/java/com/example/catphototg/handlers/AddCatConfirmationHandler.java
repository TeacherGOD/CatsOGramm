package com.example.catphototg.handlers;


import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatService;
import com.example.catphototg.service.SessionService;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AddCatConfirmationHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final CatService catService;

    @Override
    public boolean canHandle(User user, UserSession session, Update update) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_CONFIRMATION &&
                update.hasMessage() &&
                update.getMessage().hasText();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long telegramId = user.getTelegramId();

        if ("✅ Подтвердить".equals(text)) {
            Cat cat = catService.saveCat(
                    session.getCatName(),
                    session.getPhotoUrl(),
                    user
            );

            sessionService.clearSession(telegramId);

            bot.sendTextWithKeyBoard(chatId, "Котик \"" + cat.getName() + "\" успешно добавлен!",
                    bot.createMainMenuKeyboard());
        }
        else if ("❌ Отменить".equals(text)) {
            sessionService.clearSession(telegramId);
            bot.sendTextWithKeyBoard(chatId, "Добавление котика отменено",
                    bot.createMainMenuKeyboard());
        }
        else {
            bot.showCatConfirmation(chatId, session,user);
        }
    }
}