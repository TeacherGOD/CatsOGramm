package com.example.catphototg.kafka;


import java.util.List;

public record PagedResponseMy<T>(
        List<T> content,
        int currentPage,
        int totalPages,
        long totalItems,
        long chatId
) {}