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
public class MainMenuHandler implements UpdateHandler {
    private final SessionService sessionService;

    @Override
    public boolean canHandle(User user, UserSession session, Update update) {
        return (session == null || session.getState() == UserState.MAIN_MENU) &&
                update.hasMessage() &&
                update.getMessage().hasText();
    }

    @Override
    public void handle(CatBot bot, User user, UserSession session, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if ("➕ Добавить котика".equals(text)) {
            sessionService.getOrCreateSession(user, UserState.ADDING_CAT_NAME);
            bot.askForCatName(chatId, user);
        }
        else if ("🐱 Смотреть котиков".equals(text)) {
            // TODO: Реализовать позже
            bot.sendTextWithKeyBoard(chatId, "Функция просмотра котиков в разработке",
                    bot.createMainMenuKeyboard());
        }
        else if ("❤️ Мои котики".equals(text)) {
            // TODO: Реализовать позже
            bot.sendTextWithKeyBoard(chatId, "Функция 'Мои котики' в разработке",
                    bot.createMainMenuKeyboard());
        }
        else if ("/start".equals(text)) {
            bot.sendMainMenu(chatId, user);
        }
        else if ("✏️ Сменить имя".equals(text) || "/name".equals(text)) {
            bot.handleChangeNameCommand(chatId, user);
        }
        else {
            bot.sendTextWithKeyBoard(chatId, "Пожалуйста, выберите действие из меню",
                    bot.createMainMenuKeyboard());
        }
    }
}