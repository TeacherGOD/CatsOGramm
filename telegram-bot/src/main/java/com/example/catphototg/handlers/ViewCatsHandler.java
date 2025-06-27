package com.example.catphototg.handlers;

import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.Keyboard;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.*;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.example.catphototg.constants.BotConstants.*;

@Component
@RequiredArgsConstructor
public class ViewCatsHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final TelegramFacade bot;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;
    private final CatServiceClient catServiceClient;
    private final UserService userService;


    @Value("${cat.service.files-url}")
    private String filesBaseUrl;

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

        bot.sendText(chatId, messageFactory.createTextMessage(ASYNC_LOOKING_FOR_NEW_CAT, null));
        catServiceClient.getRandomCatAsync(user.getId())
                .thenCompose(randomCat -> {
                    if (randomCat == null) {
                        showNoCatsMessage(chatId);
                        return CompletableFuture.completedFuture(null);
                    }

                    var catUser = userService.findByAuthorId(randomCat.authorId());
                    sessionService.updateSession(user.getTelegramId(), s ->
                            s.setViewingCatId(randomCat.id())
                    );

                    String caption = String.format(
                            CAT_CARD_MESSAGE,
                            randomCat.name(),
                            catUser.username()
                    );

                    int likeCount = randomCat.reactionCounts().getOrDefault(ReactionType.LIKE, 0);
                    int dislikeCount = randomCat.reactionCounts().getOrDefault(ReactionType.DISLIKE, 0);
                    Keyboard keyboard = keyboardService.createReactionKeyboard(
                            randomCat.id(),
                            likeCount,
                            dislikeCount
                    );

                    MessageData messageData = messageFactory.createTextMessage(caption, keyboard);

                    // Загрузка файла и отправка фото из файла
                    return catServiceClient.getFileAsync(randomCat.filePath())
                            .thenAccept(resource -> {
                                try {
                                    File tempFile = File.createTempFile("cat", ".jpg");
                                    try (InputStream in = resource.getInputStream();
                                         OutputStream out = new FileOutputStream(tempFile)) {
                                        in.transferTo(out);
                                    }
                                    bot.sendPhotoFromFile(chatId, tempFile, messageData);
                                    tempFile.delete();
                                } catch (IOException e) {
                                    bot.handleError(chatId, "Ошибка обработки файла", e, user);
                                }
                            });
                })
                .exceptionally(ex -> {
                    Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                    bot.handleError(chatId, "Ошибка поиска котика", (Exception) cause, user);
                    return null;
                });
    }

    private void showNoCatsMessage(Long chatId) {
        MessageData noCatsMessage = messageFactory.createTextMessage(
                NO_CATS_MESSAGE,
                keyboardService.mainMenuKeyboard()
        );
        bot.sendTextWithKeyboard(chatId, noCatsMessage);
    }


}
