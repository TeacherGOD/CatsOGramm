package com.example.catphototg.kafka;

public record CatPageRequest(Long userId, String username, int page, int size,Long chatId) {
}
