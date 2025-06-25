package com.example.catphototg.catservice.dto;

import com.example.catphototg.bot.entity.User;

public record CatCreationDto(String name, String filepath, User author) {}
