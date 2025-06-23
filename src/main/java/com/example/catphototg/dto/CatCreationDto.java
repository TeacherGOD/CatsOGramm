package com.example.catphototg.dto;

import com.example.catphototg.entity.User;

public record CatCreationDto(String name, String photoUrl, User author) {}
