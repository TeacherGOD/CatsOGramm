package com.example.catphototg.service;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.Reaction;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.enums.ReactionType;
import com.example.catphototg.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;

    @Transactional
    public void toggleReaction(User user, Cat cat, ReactionType type) {
        Optional<Reaction> existingReaction = reactionRepository.findByUserAndCat(user, cat);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            Reaction newReaction = new Reaction();
            newReaction.setUser(user);
            newReaction.setCat(cat);
            newReaction.setType(type);
            reactionRepository.save(newReaction);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasReaction(User user, Cat cat, ReactionType type) {
        return reactionRepository.existsByUserAndCatAndType(user, cat, type);
    }
}
