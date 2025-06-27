package com.example.catphototg.bot.handlers;

import com.example.catphototg.bot.constants.BotConstants;
import com.example.catphototg.bot.dto.TelegramMessage;
import com.example.catphototg.bot.entity.User;
import com.example.catphototg.bot.entity.UserSession;
import com.example.catphototg.bot.entity.enums.UserState;
import com.example.catphototg.bot.entity.ui.Keyboard;
import com.example.catphototg.bot.entity.ui.MessageData;
import com.example.catphototg.bot.handlers.interfaces.TelegramFacade;
import com.example.catphototg.bot.handlers.interfaces.UpdateHandler;
import com.example.catphototg.bot.service.KeyboardService;
import com.example.catphototg.bot.service.MessageFactory;
import com.example.catphototg.bot.service.SessionService;
import com.example.catphototg.catservice.entity.ReactionType;
import com.example.catphototg.catservice.service.CatServiceClient;
import com.example.catphototg.catservice.service.RandomCatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class ViewCatsHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final RandomCatService randomCatService;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;
    private final CatServiceClient catServiceClient;

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
        //todo const
        bot.sendText(chatId, messageFactory.createTextMessage("🔍 Ищем нового котика для вас...", null));

        catServiceClient.getRandomCatAsync(user.getId())
                .thenAccept(randomCat -> {
                    if (randomCat == null) {
                        showNoCatsMessage(chatId);
                        return;
                    }

                    sessionService.updateSession(user.getTelegramId(), s ->
                            s.setViewingCatId(randomCat.id())
                    );

                    //todo const (хотя вроде уже в другом коммите)
                    String caption = String.format(
                            "🐱 Имя: %s\nАвтор: @%s",
                            randomCat.name(),
                            randomCat.authorName()
                    );

                    int likeCount = randomCat.reactionCounts().getOrDefault(ReactionType.LIKE, 0);
                    int dislikeCount = randomCat.reactionCounts().getOrDefault(ReactionType.DISLIKE, 0);
                    Keyboard keyboard = keyboardService.createReactionKeyboard(
                            randomCat.id(),
                            likeCount,
                            dislikeCount
                    );

                    bot.sendPhotoFromFile(chatId, new File(fileStorageService.load(randomCat.filePath()).toUri()),
                            new MessageData(caption, keyboard));
                })
                .exceptionally(ex -> {
                    Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                    bot.handleError(chatId, "Ошибка поиска котика", cause, user);
                    return null;
                });
    }

    private void showNoCatsMessage(Long chatId) {
        MessageData noCatsMessage = messageFactory.createTextMessage(
                "Вы посмотрели всех котиков! 🐾\nПопробуйте позже, когда добавят новых.",
                keyboardService.mainMenuKeyboard()
        );
        bot.sendTextWithKeyboard(chatId, noCatsMessage);
    }
}
