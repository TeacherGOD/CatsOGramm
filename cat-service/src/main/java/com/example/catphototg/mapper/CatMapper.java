package com.example.catphototg.mapper;

import com.example.catphototg.entity.Cat;
import com.example.common.dto.CatDto;
import com.example.common.enums.ReactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class CatMapper {

    public CatDto toDto(Cat cat, String authorName) {
        Map<ReactionType, Integer> reactionCounts = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) {
            reactionCounts.put(type, cat.getReactionCount(type));
        }

        Map<ReactionType, Boolean> userReactions = new EnumMap<>(ReactionType.class);

        for (ReactionType type : ReactionType.values()) {
            userReactions.put(type, hasUserReaction(cat, type));
        }


        return new CatDto(
                cat.getId(),
                cat.getName(),
                cat.getFilePath(),
                cat.getAuthorId(),
                authorName,
                reactionCounts,
                userReactions
        );
    }

    private boolean hasUserReaction(Cat cat, ReactionType type) {
        return cat.getReactions().stream()
                .anyMatch(r -> r.getType() == type && r.getUserId().equals(cat.getAuthorId()));
    }
}