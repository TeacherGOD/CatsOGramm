package com.example.catphototg.service;

import com.example.catphototg.entity.Reaction;
import com.example.catphototg.repository.ReactionRepository;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final CatService catService;
    private final ApplicationContext applicationContext;

    @Transactional
    public void toggleReaction(Long userId, Long catId, ReactionType type) {
        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndCatId(userId, catId);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            var cat =catService.getCatById(catId);
            Reaction newReaction = new Reaction();
            newReaction.setUserId(userId);
            newReaction.setCat(cat);
            newReaction.setType(type);
            reactionRepository.save(newReaction);
        }
    }
}
