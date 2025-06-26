package com.example.common.dto;

import com.example.common.enums.ReactionType;

import java.util.Map;

public record CatWithoutAuthorNameDto(Long id,
                                      String name,
                                      String filePath,
                                      Long authorId,
                                      Map<ReactionType, Integer> reactionCounts) {
}
