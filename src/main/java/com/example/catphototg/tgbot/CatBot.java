package com.example.catphototg.tgbot;

import com.example.catphototg.config.BotProperties;
import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.exceptions.BotOperationException;
import com.example.catphototg.handlers.interfaces.BotOperations;
import com.example.catphototg.service.DispatcherService;
import com.example.catphototg.service.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Component
@Slf4j
public class CatBot extends TelegramLongPollingBot  implements BotOperations {
    private final BotProperties botProperties;
    private final DispatcherService dispatcher;
    private final KeyboardService keyboardService;

    public CatBot(BotProperties botProperties, @Lazy DispatcherService dispatcher, KeyboardService keyboardService) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.dispatcher = dispatcher;
        this.keyboardService = keyboardService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            dispatcher.dispatch(this, update);
        } catch (Exception e) {
            log.error("Ошибка обработки обновления", e);
        }
    }

    public void showMainMenu(Long chatId, User user) {
        sendTextWithKeyboard(chatId, formatMainMenuMessage(user), keyboardService.mainMenuKeyboard());
    }

    private String formatMainMenuMessage(User user) {
        return user.getDisplayName() + ", выбери действие:";
    }

    public void askForName(Long chatId) {
        sendTextWithKeyboard(chatId, BotConstants.NAME_REGISTRATION_PROMPT, keyboardService.cancelKeyboard());
    }

    public void askForCatName(Long chatId, User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_NAME_PROMPT;
        sendTextWithKeyboard(chatId, message, keyboardService.cancelKeyboard());
    }

    public void askForCatPhoto(Long chatId, User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_PHOTO_PROMPT;
        sendTextWithKeyboard(chatId, message, keyboardService.cancelKeyboard());
    }

    public void showCatConfirmation(Long chatId, UserSession session, User user) {
        try {
            String caption =
                    user.getDisplayName() + ", " + BotConstants.CAT_CONFIRMATION_PROMPT+
                    session.getCatName();

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(session.getPhotoFileId()));
            photo.setCaption(caption);
            photo.setReplyMarkup(keyboardService.confirmationKeyboard());
            execute(photo);
        } catch (TelegramApiException e) {
            handleError(chatId, BotConstants.PHOTO_SENDING_ERROR, e,user);
        }
    }

    public String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return execute(getFile).getFilePath();
    }




    public void sendTextWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setReplyMarkup(keyboard);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    public void handleError(Long chatId, String message, Exception e,User user) {
        log.error(message, e);
        sendTextWithKeyboard(chatId,
                "😿 Упс, "+user.getDisplayName()+", произошла ошибка: " + e.getMessage(),
                keyboardService.mainMenuKeyboard());
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }


    @Override
    public File downloadBotFile(String filePath) throws BotOperationException {
        try {
            return super.downloadFile(filePath);
        } catch (TelegramApiException e) {
            throw new BotOperationException("Ошибка загрузки файла", e);
        }
    }

}
