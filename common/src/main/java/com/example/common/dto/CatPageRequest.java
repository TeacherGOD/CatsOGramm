package com.example.common.dto;

public record CatPageRequest(Long userId,String username,int page,int size) {
}
