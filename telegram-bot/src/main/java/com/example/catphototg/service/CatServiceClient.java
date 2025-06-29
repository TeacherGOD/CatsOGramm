package com.example.catphototg.service;


import com.example.catphototg.kafka.CatDto;
import com.example.catphototg.kafka.CatPageRequest;
import com.example.catphototg.kafka.KafkaSender;
import com.example.catphototg.kafka.UserReaction;
import com.example.common.dto.CatCreationDto;
import com.example.common.dto.CatWithoutAuthorNameDto;
import com.example.common.dto.PagedResponse;
import com.example.common.enums.ReactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import static com.example.catphototg.constants.BotConstants.URL_CLIENT;

@Service

public class CatServiceClient {

    private final WebClient webClient;
    @Autowired
    private final FileStorageService fileStorageService;
    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private SessionService sessionService;


    public CatServiceClient(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.webClient = WebClient.create(URL_CLIENT);
    }

    public void addCatAsync(CatCreationDto dto) {
        kafkaSender.sendAddCat(dto);
    }

    public void deleteCatAsync(Long catId, Long userId) {

        kafkaSender.sendDeleteCat(
                new CatDto(
                        catId,
                        null,
                        null,
                        userId,
                        null,
                        null,
                        null,
                        null));


        fileStorageService.delete(filename);
    }

    public void getCatsCountAsync(Long userId) {
        kafkaSender.sendCountCatsAsk(userId);
    }

    public CompletableFuture<PagedResponse<CatDto>> getCatsByAuthorAsync(Long userId, String username, int page, int size, Long chatId) {
        kafkaSender.sendGetAllMyCat(new CatPageRequest(userId,username,page,size,chatid));
    }

    public CompletableFuture<CatDto> getCatByChatIdAsync(Long catId, String username) {
        kafkaSender.sendCatDetails(new CatDto(catId,username,null,null,null,null,null));
    }

    public CompletableFuture<CatWithoutAuthorNameDto> getRandomCatAsync(
            Long userId
    ) {
        kafkaSender.sendGetRandomCat(userId);
    }

    public void updateReactionAsync(Long catId, Long userId, ReactionType type) {
        kafkaSender.sendMakeReaction(new UserReaction(catId,userId,type));
    }



}
