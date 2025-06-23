package com.example.catphototg.tgbot;

import com.example.catphototg.config.BotProperties;
import com.example.catphototg.constants.BotConstants;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.service.DispatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CatBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final DispatcherService dispatcher;

    public CatBot(BotProperties botProperties, DispatcherService dispatcher) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.dispatcher = dispatcher;
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
        sendTextWithKeyboard(chatId, formatMainMenuMessage(user), createMainMenuKeyboard());
    }

    private String formatMainMenuMessage(User user) {
        return user.getDisplayName() + ", выбери действие:";
    }

    public void askForName(Long chatId) {
        sendTextWithKeyboard(chatId, BotConstants.NAME_REGISTRATION_PROMPT, createCancelKeyboard());
    }

    public void askForCatName(Long chatId, User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_NAME_PROMPT;
        sendTextWithKeyboard(chatId, message, createCancelKeyboard());
    }

    public void askForCatPhoto(Long chatId, User user) {
        String message = user.getDisplayName() + ", " + BotConstants.CAT_PHOTO_PROMPT;
        sendTextWithKeyboard(chatId, message, createCancelKeyboard());
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
            photo.setReplyMarkup(createConfirmationKeyboard());
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

    public InlineKeyboardMarkup createCancelKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(BotConstants.CANCEL_BUTTON, BotConstants.CANCEL_ACTION));

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup createConfirmationKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(BotConstants.CONFIRM_BUTTON, BotConstants.CONFIRM_CAT_ACTION));
        row.add(createButton(BotConstants.CANCEL_BUTTON, BotConstants.CANCEL_CAT_ACTION));

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(BotConstants.SHOW_CATS_COMMAND, BotConstants.VIEW_CATS_ACTION));
        row1.add(createButton(BotConstants.ADD_CAT_COMMAND, BotConstants.ADD_CAT_ACTION));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(BotConstants.MY_CATS_COMMAND, BotConstants.MY_CATS_ACTION));
        row2.add(createButton(BotConstants.CHANGE_NAME_COMMAND, BotConstants.CHANGE_NAME_ACTION));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
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
                createMainMenuKeyboard());
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

}
