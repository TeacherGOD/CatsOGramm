package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.constants.BotConstants;
import com.example.catphototg.catservice.dto.CatDto;
import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.catservice.entity.ReactionType;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.Keyboard;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.KeyboardService;
import com.example.catphototg.bot.service.MessageFactory;
import com.example.catphototg.catservice.service.RandomCatService;
import com.example.catphototg.bot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ViewCatsHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final RandomCatService randomCatService;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;
    private final FileStorageService fileStorageService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return message.isCallback() && BotConstants.VIEW_CATS_ACTION.equals(message.text());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        sessionService.updateSession(user.getTelegramId(), s -> s.setState(UserState.VIEWING_RANDOM_CAT));
        showRandomCat(user, message.chatId());
    }

    public void showRandomCat(User user, Long chatId) {
        Optional<CatDto> randomCat = randomCatService.getRandomCatForUser(user);

        if (randomCat.isEmpty()) {
            MessageData noCatsMessage = messageFactory.createTextMessage(
                    NO_CATS_MESSAGE,
                    keyboardService.mainMenuKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, noCatsMessage);
            sessionService.clearSession(user.getTelegramId());
            return;
        }

        var cat = randomCat.get();
        String caption = String.format(
                CAT_CARD_MESSAGE,
                cat.name(),
                cat.authorName()
        );

        sessionService.updateSession(user.getTelegramId(), s ->
                s.setViewingCatId(cat.id())
        );

        int likeCount = cat.reactionCounts().getOrDefault(ReactionType.LIKE, 0);
        int dislikeCount = cat.reactionCounts().getOrDefault(ReactionType.DISLIKE, 0);
        Keyboard keyboard = keyboardService.createReactionKeyboard(cat.id(), likeCount, dislikeCount);

        File photoFile = new File(fileStorageService.load(cat.filePath()).toUri());
        bot.sendPhotoFromFile(chatId, photoFile, new MessageData(caption, keyboard));
    }
}
