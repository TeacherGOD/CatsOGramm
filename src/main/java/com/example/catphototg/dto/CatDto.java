package com.example.catphototg.dto;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.enums.ReactionType;

import java.util.EnumMap;
import java.util.Map;

public record CatDto(
        Long id,
        String name,
        String filePath,
        String authorName,
        Map<ReactionType, Integer> reactionCounts,
        Map<ReactionType, Boolean> userReactions
) {
    public static CatDto fromEntity(Cat cat, Long userId) {
        Map<ReactionType, Integer> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) {
            counts.put(type, cat.getReactionCount(type));
        }

        Map<ReactionType, Boolean> userReactions = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) {
            userReactions.put(type,
                    cat.getReactions().stream()
                            .anyMatch(r -> r.getType() == type &&
                                    r.getUser().getId().equals(userId)));
        }

        return new CatDto(
                cat.getId(),
                cat.getName(),
                cat.getFilePath(),
                cat.getAuthor().getUsername(),
                counts,
                userReactions
        );
    }
}