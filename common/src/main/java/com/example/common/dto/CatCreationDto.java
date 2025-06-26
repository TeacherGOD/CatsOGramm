package com.example.common.dto;

public record CatCreationDto(String name,
                             String filepath,
                             Long authorId,
                             String authorName) {}
