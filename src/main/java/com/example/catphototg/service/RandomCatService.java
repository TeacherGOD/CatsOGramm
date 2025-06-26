package com.example.catphototg.service;

import com.example.catphototg.dto.CatDto;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.enums.ReactionType;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.exceptions.ReactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RandomCatService {
    private final CatService catService;
    private final ReactionService reactionService;

    @Transactional(readOnly = true)
    public Optional<CatDto> getRandomCatForUser(User user) {
        return catService.findRandomUnseenCat(user.getId())
                .map(cat -> CatDto.fromEntity(cat, user.getId()));
    }

    public void processReaction(User user, Long catId, ReactionType type) throws ReactionException {
        Cat cat;
        try
        {
            cat = catService.getCatById(catId);
            reactionService.toggleReaction(user, cat, type);
        } catch (CatNotFoundException e) {
            throw new ReactionException("Ошибка во время реакции: ",e);
        }

    }
}
