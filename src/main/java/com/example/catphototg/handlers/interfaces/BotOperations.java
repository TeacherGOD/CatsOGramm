package com.example.catphototg.handlers.interfaces;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.UserSession;
import com.example.catphototg.exceptions.BotOperationException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public interface BotOperations {
    void sendTextWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard);
    void showMainMenu(Long chatId, User user);
    void askForName(Long chatId);
    void askForCatName(Long chatId, User user);
    void askForCatPhoto(Long chatId, User user);
    void showCatConfirmation(Long chatId, UserSession session, User user);
    void handleError(Long chatId, String message, Exception e, User user);
    String getFilePath(String fileId) throws TelegramApiException;
    File downloadBotFile(String filePath) throws BotOperationException;
}
