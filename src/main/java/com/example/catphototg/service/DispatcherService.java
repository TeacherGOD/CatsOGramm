package com.example.catphototg.service;

import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.mapper.TelegramMessageMapper;
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
    private final TelegramMessageMapper telegramMessageMapper;

    public void dispatch(CatBot bot, Update update) {
        TelegramMessage message = telegramMessageMapper.toDto(update);
        if (message == null) return;

        processUpdate(bot, message);
    }

    private void processUpdate(CatBot bot, TelegramMessage tgMessage) {
        User user = userService.findByTelegramId(tgMessage.userId()).orElseGet(()->userService.createUser(tgMessage.userId(),tgMessage.username()));


        Optional<UserSession> sessionOpt = sessionService.findByUserTelegramId(tgMessage.userId());

        if (user.getDisplayName() == null && sessionOpt.isEmpty()) {
            sessionService.getOrCreateSession(user, UserState.REGISTERING_NAME);
            bot.askForName(tgMessage.chatId());
            return;
        }

        UserSession session = sessionOpt.orElse(null);
        for (UpdateHandler handler : handlers) {
            if (handler.canHandle(user, session, tgMessage)) {
                handler.handle(bot, user, session, tgMessage);
                return;
            }
        }
        restoreContext(bot, tgMessage.chatId(), user, session);
    }

    private void restoreContext(CatBot bot, Long chatId, User user, UserSession session) {
        if (session == null) {
            bot.showMainMenu(chatId, user);
            return;
        }

        switch (session.getState()) {
            case ADDING_CAT_NAME:
                bot.askForCatName(chatId, user);
                break;
            case ADDING_CAT_PHOTO:
                bot.askForCatPhoto(chatId, user);
                break;
            case ADDING_CAT_CONFIRMATION:
                bot.showCatConfirmation(chatId, session, user);
                break;
            case REGISTERING_NAME,CHANGING_NAME:
                bot.askForName(chatId);
                break;
            case BROWSING_MY_CATS:
                // TODO: Реализовать позже
                bot.sendTextWithKeyboard(chatId, "Продолжаем просмотр ваших котиков...",
                        bot.createMainMenuKeyboard());
                break;
            default:
                bot.showMainMenu(chatId, user);
        }
    }
}