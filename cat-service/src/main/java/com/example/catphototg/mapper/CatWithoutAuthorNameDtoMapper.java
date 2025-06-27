package com.example.catphototg.mapper;

import com.example.catphototg.entity.Cat;
import com.example.common.dto.CatWithoutAuthorNameDto;
import com.example.common.enums.ReactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class CatWithoutAuthorNameDtoMapper {

    public static CatWithoutAuthorNameDto toDto(Cat cat) {

        Map<ReactionType, Integer> reactionCounts = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) {
            int count = (int) cat.getReactions().stream()
                    .filter(r -> r.getType() == type)
                    .count();
            reactionCounts.put(type, count);
        }

        return new CatWithoutAuthorNameDto(
                cat.getId(),
                cat.getName(),
                cat.getFilePath(),
                cat.getAuthorId(),
                reactionCounts
        );
    }

    private CatWithoutAuthorNameDtoMapper() {
    }
}
