package com.example.catphototg.kafka;

import com.example.common.enums.ReactionType;

public record UserReaction(Long catId, Long userId, ReactionType type) {
}
