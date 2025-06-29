package com.example.catphototg.kafka;

public record CatsMenuDto(Long chatId, Long userId, Long telegramId, String username, int count) {
}
