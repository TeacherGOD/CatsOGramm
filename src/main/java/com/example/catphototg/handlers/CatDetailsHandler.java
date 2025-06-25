package com.example.catphototg.handlers;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatDetailsHandler implements UpdateHandler {
    private final CatCardService catCardService;
    private final TelegramFacade bot;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return message.isCallback() &&
                message.text().startsWith(BotConstants.CAT_DETAILS_PREFIX);
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String catIdStr = message.text().substring(BotConstants.CAT_DETAILS_PREFIX.length());
        long catId = Long.parseLong(catIdStr);

        catCardService.showCatCard(
                bot,
                user,
                catId,
                session.getCurrentPage(),
                message.chatId()
        );
    }
}