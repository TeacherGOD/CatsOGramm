package com.example.catphototg.catservice.service;

import com.example.catphototg.catservice.dto.CatCreationDto;
import com.example.catphototg.catservice.dto.CatDto;
import com.example.catphototg.catservice.entity.ReactionType;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class CatServiceClient {
    @Value("${cat.service.url}")
    private WebClient webClient;



    @Async("asyncTaskExecutor")
    public CompletableFuture<CatDto> addCatAsync(CatCreationDto dto) {
        return webClient.post()
                .uri("/api/cats")
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка сервиса котиков: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(CatDto.class)
                .timeout(Duration.ofSeconds(15))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Boolean> deleteCatAsync(Long catId, Long userId) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/{id}")
                        .queryParam("userId", userId)
                        .build(catId))
                .retrieve()
                .toBodilessEntity()
                .toFuture()
                .thenApply(response -> true)
                .exceptionally(ex -> false);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Integer> getCatsCountAsync(Long userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/count")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка подсчета котиков: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(Integer.class)
                .timeout(Duration.ofSeconds(5))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Page<CatDto>> getCatsByAuthorAsync(Long userId, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats")
                        .queryParam("userId", userId)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка получения котиков: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(new ParameterizedTypeReference<PageImpl<CatDto>>() {})
                .timeout(Duration.ofSeconds(10))
                .map(pageImpl -> (Page<CatDto>) pageImpl)
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<CatDto> getCatByIdAsync(Long catId) {
        return webClient.get()
                .uri("/api/cats/{id}", catId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка получения котика: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(CatDto.class)
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<CatDto> getRandomCatAsync(Long userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/random")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка получения случайного котика: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(CatDto.class)
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Void> updateReactionAsync(Long catId, Long userId, ReactionType type) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/{id}/reaction")
                        .queryParam("userId", userId)
                        .queryParam("type", type.toString())
                        .build(catId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка обновления реакции: " + response.statusCode() + " - " + error
                                )))
                )
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .then()
                .toFuture();
    }
}
