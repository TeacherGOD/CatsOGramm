package com.example.catphototg.service;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.tgbot.CatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DispatcherService {
    private final List<UpdateHandler> handlers;
    private final SessionService sessionService;
    private final UserService userService;

    public void dispatch(CatBot bot, Update update) {
        if (!update.hasMessage() || !(update.getMessage().hasText() || update.getMessage().hasPhoto())) {
            return;
        }

        Long telegramId = update.getMessage().getFrom().getId();
        User user = userService.findOrCreateUser(update.getMessage().getFrom());
        Optional<UserSession> sessionOpt = sessionService.findByUserTelegramId(telegramId);

        if (user.getDisplayName() == null && sessionOpt.isEmpty()) {
            sessionService.getOrCreateSession(user, UserState.REGISTERING_NAME);
            bot.askForName(update.getMessage().getChatId());
            return;
        }

        UserSession session = sessionOpt.orElse(null);
        for (UpdateHandler handler : handlers) {
            if (handler.canHandle(user, session, update)) {
                handler.handle(bot, user, session, update);
                return;
            }
        }

        String errorMessage = "Команда не распознана. Пожалуйста, используйте меню.";
        if (user.getDisplayName() != null) {
            bot.sendPersonalizedMessage(update.getMessage().getChatId(), user,
                    "{name}, " + errorMessage, bot.createMainMenuKeyboard());
        } else {
            bot.sendText(update.getMessage().getChatId(), errorMessage,
                    bot.createMainMenuKeyboard());
        }
    }
}