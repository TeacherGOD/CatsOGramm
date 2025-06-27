package com.example.catphototg.catservice.dto;

public record CatCreationDto(String name,
                             String filepath,
                             Long authorId,
                             String authorName) {}
