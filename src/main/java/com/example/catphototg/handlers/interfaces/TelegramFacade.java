package com.example.catphototg.handlers.interfaces;

import com.example.catphototg.entity.User;
import com.example.catphototg.entity.ui.MessageData;
import com.example.catphototg.exceptions.BotOperationException;

import java.io.File;

public interface TelegramFacade {
    void showMainMenu(Long chatId, User user);
    void askForName(Long chatId);
    void askForCatName(Long chatId, User user);
    void askForCatPhoto(Long chatId, User user);
    void sendTextWithKeyboard(Long chatId, MessageData messageData);
    void handleError(Long chatId, String message, Exception e, User user);
    String getFilePath(String fileId) throws BotOperationException;
    File downloadBotFile(String filePath) throws BotOperationException;
    void sendPhotoWithKeyboard(Long chatId, String photoFileId, MessageData messageData);
    void sendPhotoFromFile(Long chatId, String filePath, MessageData messageData);
    void sendText(Long chatId, MessageData textMessage);
}
