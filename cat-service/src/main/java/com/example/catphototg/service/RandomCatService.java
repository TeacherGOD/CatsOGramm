package com.example.catphototg.service;


import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.exceptions.ReactionException;
import com.example.common.dto.CatWithoutAuthorNameDto;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomCatService {
    private final CatService catService;
    private final ReactionService reactionService;


    @Transactional(readOnly = true)
    public Optional<CatWithoutAuthorNameDto> getRandomCatForUserId(Long userId) {
        return catService.findRandomUnseenCat(userId);
    }

    public void processReaction(Long userId, Long catId, ReactionType type) throws ReactionException {
        try
        {
            reactionService.toggleReaction(userId, catId, type);
        } catch (CatNotFoundException e) {
            throw new ReactionException("Ошибка во время реакции: ",e);
        }
    }
}
