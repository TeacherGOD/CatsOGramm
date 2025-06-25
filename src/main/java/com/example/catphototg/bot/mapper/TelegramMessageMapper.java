package com.example.catphototg.bot.mapper;

import com.example.catphototg.bot.dto.TelegramMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Comparator;
import java.util.Optional;

@Component
public class TelegramMessageMapper {

    public TelegramMessage toDto(Update update) {
        if (update.hasCallbackQuery()) {
            return toDto(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            return toDto(update.getMessage());
        }
        return null;
    }

    private TelegramMessage toDto(Message message) {
        Long chatId = message.getChatId();
        User from = message.getFrom();

        return new TelegramMessage(
                chatId,
                from.getId(),
                message.getText(),
                extractBestPhotoFileId(message),
                from.getUserName(),
                false
        );
    }

    private TelegramMessage toDto(CallbackQuery callbackQuery) {
        Message message = (Message) callbackQuery.getMessage();
        User from = callbackQuery.getFrom();

        return new TelegramMessage(
                message.getChatId(),
                from.getId(),
                callbackQuery.getData(),
                null,
                from.getUserName(),
                true
        );
    }

    private String extractBestPhotoFileId(Message message) {
        return Optional.ofNullable(message.getPhoto())
                .flatMap(photos -> photos.stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .map(PhotoSize::getFileId))
                .orElse(null);
    }

}
