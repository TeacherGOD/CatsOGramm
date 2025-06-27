package com.example.catphototg.catservice.service;

import com.example.catphototg.bot.entity.User;
import com.example.catphototg.catservice.entity.Cat;
import com.example.catphototg.catservice.entity.Reaction;
import com.example.catphototg.catservice.entity.ReactionType;
import com.example.catphototg.catservice.repository.ReactionRepository;
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
}
