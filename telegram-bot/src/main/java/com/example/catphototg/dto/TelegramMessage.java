package com.example.catphototg.dto;

public record TelegramMessage(
        Long chatId,
        Long userId,
        String text,
        String photoFileId,
        String username,
        boolean isCallback
) {
    public boolean hasText(){
        return text!=null && !text.isBlank();
    }

    public boolean hasPhoto() {
        return photoFileId!=null&&!photoFileId.isBlank();
    }
}
