package com.example.catphototg.kafka;

public record CatDetailsKafka(Long telegramId, Long catId, String catName, String filePath, Long chatId, Long userId) {
}
