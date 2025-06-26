package com.example.common.dto;

import com.example.common.enums.ReactionType;
import java.util.Map;

public record CatDto(
        Long id,
        String name,
        String filePath,
        Long authorId,
        String authorName,
        Map<ReactionType, Integer> reactionCounts,
        Map<ReactionType, Boolean> userReactions
) {}