package com.example.catphototg.handlers;


import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatServiceClient;
import com.example.catphototg.service.SessionService;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.catphototg.constants.BotConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReactionHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final CatServiceClient catServiceClient;
    private final ViewCatsHandler viewCatsHandler;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        if (session == null || session.getState() != UserState.VIEWING_RANDOM_CAT) {
            return false;
        }
        return message.isCallback() && (
                message.text().startsWith(LIKE_ACTION_PREFIX) ||
                        message.text().startsWith(DISLIKE_ACTION_PREFIX) ||
                        NEXT_CAT_ACTION.equals(message.text())
        )||message.text().equals(BACK_TO_MENU_ACTION);
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        Long currentCatId = session.getViewingCatId();
        String callback = message.text();
        try {
            if (callback.startsWith(LIKE_ACTION_PREFIX)) {
                updateReaction(user, message.chatId(), currentCatId, ReactionType.LIKE);
            }
            else if (callback.startsWith(DISLIKE_ACTION_PREFIX)) {
                updateReaction(user, message.chatId(), currentCatId, ReactionType.DISLIKE);
            } else if (callback.equals(BACK_TO_MENU_ACTION)) {
                sessionService.clearSession(user.getTelegramId());
                bot.showMainMenu(message.chatId(), user);
                return;
            }
            sessionService.updateSession(user.getTelegramId(), s ->
                    s.setViewingCatId(null)
            );
            viewCatsHandler.showRandomCatPrepare(user, message.chatId());
        } catch (Exception e) {
            bot.handleError(message.chatId(), "Ошибка во время реакции: ", e,user);
        }
    }
    private void updateReaction(User user, Long chatId, Long catId, ReactionType type) {
        catServiceClient.updateReactionAsync(catId, user.getId(), type);
    }
}