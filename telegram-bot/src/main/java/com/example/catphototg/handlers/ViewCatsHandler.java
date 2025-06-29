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
import com.example.catphototg.kafka.CatDto;
import com.example.catphototg.service.*;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private final FileStorageService fileStorageService;


    @Value("${cat.service.files-url}")
    private String filesBaseUrl;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return message.isCallback() && BotConstants.VIEW_CATS_ACTION.equals(message.text());
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        sessionService.updateSession(user.getTelegramId(), s -> s.setState(UserState.VIEWING_RANDOM_CAT));
        showRandomCatPrepare(user, message.chatId());
    }

    public void showRandomCatPrepare(User user, Long chatId) {

        bot.sendText(chatId, messageFactory.createTextMessage(ASYNC_LOOKING_FOR_NEW_CAT, null));
        catServiceClient.getRandomCatAsync(user.getId());



    }

public void showRandomCat(CatDto randomCat){
    try {
        var catUser = userService.findByAuthorId(randomCat.authorId());
        sessionService.updateSession(catUser.telegramId(), s ->
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

        try {
            var photo = fileStorageService.load(randomCat.filePath());
            bot.sendPhotoFromFile(randomCat.chatId(), photo.toFile(), messageData);
            fileStorageService.delete(photo.getFileName().toString());
        } catch (Exception e) {
            bot.handleError(randomCat.chatId(), "Ошибка получения файла", e, null);
        }
    } catch (Exception ex) {
        bot.handleError(randomCat.chatId(), "Ошибка поиска котика", ex, null);
    }
}

    private void showNoCatsMessage(Long chatId) {
        MessageData noCatsMessage = messageFactory.createTextMessage(
                NO_CATS_MESSAGE,
                keyboardService.mainMenuKeyboard()
        );
        bot.sendTextWithKeyboard(chatId, noCatsMessage);
    }


}
