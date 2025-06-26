package com.example.catphototg.catservice.service;

import com.example.catphototg.catservice.dto.CatCreationDto;
import com.example.catphototg.catservice.dto.CatDto;
import org.hibernate.service.spi.ServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class CatServiceClient {
    private final WebClient webClient;

    public CatServiceClient() {
        this.webClient = WebClient.create("http://localhost:8081"); // URL cat-service
    }

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
                .toFuture();
    }

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
}
