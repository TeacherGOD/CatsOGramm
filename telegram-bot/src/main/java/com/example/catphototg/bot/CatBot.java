package com.example.catphototg.bot;

import com.example.catphototg.config.BotProperties;
import com.example.catphototg.entity.User;
import com.example.catphototg.utils.KeyboardConverter;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.exceptions.BotOperationException;
import com.example.catphototg.handlers.interfaces.TelegramFacade;
import com.example.catphototg.service.DispatcherService;
import com.example.catphototg.service.MessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Component
@Slf4j
public class CatBot extends TelegramLongPollingBot  implements TelegramFacade {
    private final BotProperties botProperties;
    private final DispatcherService dispatcher;
    private final MessageFactory messageFactory;
    private final KeyboardConverter keyboardConverter;

    public CatBot(BotProperties botProperties, @Lazy DispatcherService dispatcher, MessageFactory messageFactory, KeyboardConverter keyboardConverter) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.dispatcher = dispatcher;
        this.messageFactory = messageFactory;
        this.keyboardConverter = keyboardConverter;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            dispatcher.dispatch(this, update);
        } catch (Exception e) {
            log.error("Ошибка обработки обновления", e);
        }
    }

    @Override
    public void showMainMenu(Long chatId, User user) {
        sendTextWithKeyboard(chatId, messageFactory.createMainMenuMessage(user));
    }

    @Override
    public void askForName(Long chatId) {
        sendTextWithKeyboard(chatId, messageFactory.createNameRegistrationPrompt());
    }

    @Override
    public void askForCatName(Long chatId, User user) {
        sendTextWithKeyboard(chatId, messageFactory.createCatNamePrompt(user));
    }

    @Override
    public void askForCatPhoto(Long chatId, User user) {
        sendTextWithKeyboard(chatId, messageFactory.createCatPhotoPrompt(user));
    }

    public String getFilePath(String fileId) throws BotOperationException  {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            return execute(getFile).getFilePath();
        } catch (TelegramApiException e) {
            throw new BotOperationException("Ошибка получения пути файла", e);
        }
    }

    @Override
    public void sendTextWithKeyboard(Long chatId, MessageData messageData) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(messageData.text());
            message.setReplyMarkup(keyboardConverter.convert(messageData.keyboard()));
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Ошибка отправки сообщения", ex);
        }
    }

    public void sendText(Long chatId, MessageData messageData){
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(messageData.text());
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Ошибка отправки сообщения", ex);
        }
    }


    public void sendPhotoWithKeyboard(Long chatId, String photoFileId, MessageData messageData) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(photoFileId));
            photo.setCaption(messageData.text());
            photo.setReplyMarkup(keyboardConverter.convert(messageData.keyboard()));

            execute(photo);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка отправки фото", e, null);
        }
    }
    public void sendPhotoFromFile(Long chatId, File photoFile, MessageData messageData) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(photoFile));
            photo.setCaption(messageData.text());
            photo.setReplyMarkup(keyboardConverter.convert(messageData.keyboard()));
            execute(photo);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка отправки фото с диска", e, null);
            log.error(String.format("Filepath=%s,messageData=%s",photoFile.toURI(),messageData.text()));
        }
    }

    @Override
    public void handleError(Long chatId, String contextMessage, Exception e, User user) {
        log.error(contextMessage, e);
        MessageData errorMessage = messageFactory.createErrorMessage(user, e.getMessage());
        sendTextWithKeyboard(chatId, errorMessage);
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
    public File downloadBotFile(String fileId) throws BotOperationException {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            String filePath = execute(getFile).getFilePath();

            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            throw new BotOperationException("Ошибка загрузки файла", e);
        }
    }

    public void sendPhotoWithUrl(Long chatId, String photoUrl, MessageData messageData) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(photoUrl));
            photo.setCaption(messageData.text());
            photo.setReplyMarkup(keyboardConverter.convert(messageData.keyboard()));

            execute(photo);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка отправки фото по URL", e, null);
        }
    }
}
