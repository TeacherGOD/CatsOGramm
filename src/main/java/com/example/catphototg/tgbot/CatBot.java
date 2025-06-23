package com.example.catphototg.tgbot;

import com.example.catphototg.config.BotProperties;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.entity.enums.UserState;
import com.example.catphototg.service.DispatcherService;
import com.example.catphototg.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CatBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final DispatcherService dispatcher;
    private final SessionService sessionService;

    public CatBot(BotProperties botProperties, DispatcherService dispatcher, SessionService sessionService) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.dispatcher = dispatcher;
        this.sessionService = sessionService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            dispatcher.dispatch(this, update);
        } catch (Exception e) {
            log.error("Ошибка обработки обновления", e);
        }
    }

    public void sendPersonalizedMessage(Long chatId, User user, String messageText,
                                        ReplyKeyboardMarkup keyboard) {
        String personalizedText = personalizeMessage(user, messageText);
        sendText(chatId, personalizedText, keyboard);
    }
    public void sendText(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        executeMessage(message);
    }
    private String personalizeMessage(User user, String text) {
        String name = user.getDisplayName() != null ?
                user.getDisplayName() :
                "Друг";

        return text.replace("{name}", name);
    }

    public void askForCatName(Long chatId, User user) {
        sendPersonalizedMessage(chatId, user,
                "{name}, введите имя котика:", createCancelKeyboard());
    }

    public void askForCatPhoto(Long chatId, User user) {
        sendPersonalizedMessage(chatId, user,
                "{name}, отправьте фото котика:", createCancelKeyboard());
    }

    public void showCatConfirmation(Long chatId, UserSession session, User user) {
        if (session.getPhotoFileId() == null || session.getPhotoFileId().isEmpty()) {
            handleError(chatId, "Не удалось загрузить фото",
                    new RuntimeException("Photo file id is empty"), user);
            return;
        }
        try {
            String caption = personalizeMessage(user,
                    "{name}, подтвердите добавление котика:\nИмя: " + session.getCatName());

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(session.getPhotoFileId()));
            photo.setCaption(caption);
            photo.setReplyMarkup(createConfirmationKeyboard());
            photo.validate();
            execute(photo);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка отправки фото", e,user);
        }
    }
    public String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return execute(getFile).getFilePath();
    }


    public void sendTextWithKeyBoard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
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


    public ReplyKeyboardMarkup createCancelKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("❌ Отмена");
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public ReplyKeyboardMarkup createConfirmationKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("✅ Подтвердить");
        row.add("❌ Отменить");
        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }
    public void sendMainMenu(Long chatId, User user) {
        sendPersonalizedMessage(chatId, user,
                "{name}, выбери действие:", createMainMenuKeyboard());
    }
    public void handleError(Long chatId, String message, Exception e, User user) {
        log.error(message, e);
        sendPersonalizedMessage(chatId, user,
                "😿 Упс, {name}, произошла ошибка: " + e.getMessage(),
                createMainMenuKeyboard());
    }
    public ReplyKeyboardMarkup createMainMenuKeyboard() {


        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("🐱 Смотреть котиков");
        row1.add("➕ Добавить котика");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("❤️ Мои котики");
        row2.add("✏️ Сменить имя");

        rows.add(row1);
        rows.add(row2);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public void askForName(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Привет! Как тебя зовут?");
        message.setReplyMarkup(createCancelKeyboard());
        executeMessage(message);
    }
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
    public void sendDefaultMessage(Long chatId, User user) {
        sendPersonalizedMessage(chatId, user,
                "{name}, я не понял твою команду. Пожалуйста, используй меню:",
                createMainMenuKeyboard());
    }

    public void handleChangeNameCommand(Long chatId, User user) {
        sessionService.getOrCreateSession(user, UserState.CHANGING_NAME);
        sessionService.updateSession(user.getId(), s->s.setState(UserState.CHANGING_NAME));
        askForName(chatId);
    }

    @Override
    public String getBotUsername() {
        return botProperties.getToken();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
