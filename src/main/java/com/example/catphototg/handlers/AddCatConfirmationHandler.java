package com.example.catphototg.handlers;


import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.dto.CatCreationDto;
import com.example.catphototg.dto.TelegramMessage;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.handlers.interfaces.UpdateHandler;
import com.example.catphototg.service.CatService;
import com.example.catphototg.service.KeyboardService;
import com.example.catphototg.service.MessageFactory;
import com.example.catphototg.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddCatConfirmationHandler implements UpdateHandler {
    private final SessionService sessionService;
    private final CatService catService;
    private final TelegramFacade bot;
    private final MessageFactory messageFactory;
    private final KeyboardService keyboardService;

    @Override
    public boolean canHandle(User user, UserSession session, TelegramMessage message) {
        return session != null &&
                session.getState() == UserState.ADDING_CAT_CONFIRMATION &&
                message.isCallback();
    }

    @Override
    public void handle(User user, UserSession session, TelegramMessage message) {
        String text = message.text();
        Long chatId = message.chatId();
        Long telegramId = user.getTelegramId();

        if (BotConstants.CONFIRM_CAT_ACTION.equals(text)) {
            Cat cat = catService.saveCat(new CatCreationDto(
                    session.getCatName(),
                    session.getFilePath(),
                    user));

            sessionService.clearSession(telegramId);

            String successText = "Котик \"" + cat.getName() + "\" успешно добавлен!";
            MessageData successMessage = messageFactory.createTextMessage(
                    successText,
                    keyboardService.mainMenuKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, successMessage);
        }
        else if (BotConstants.CANCEL_CAT_ACTION.equals(text)) {
            sessionService.clearSession(telegramId);
            MessageData cancelMessage = messageFactory.createTextMessage(
                    "Добавление котика отменено",
                    keyboardService.mainMenuKeyboard()
            );
            bot.sendTextWithKeyboard(chatId, cancelMessage);
        }
        else {
            bot.showCatConfirmation(chatId, session,user);
        }
    }
}