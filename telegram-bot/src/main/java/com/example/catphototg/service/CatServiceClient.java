package com.example.catphototg.service;


import com.example.common.dto.CatCreationDto;
import com.example.common.dto.CatDto;
import com.example.common.dto.CatWithoutAuthorNameDto;
import com.example.common.dto.PagedResponse;
import com.example.common.enums.ReactionType;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.example.catphototg.constants.BotConstants.USER_ID_PARAM;

@Service
public class CatServiceClient {
    @Value("${cat.service.url}")
    private String urlToWebClient;

    private final WebClient webClient;


    public CatServiceClient() {
        this.webClient = WebClient.create(urlToWebClient);
    }

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
                        .queryParam(USER_ID_PARAM, userId)
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
                        .queryParam(USER_ID_PARAM, userId)
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
    public CompletableFuture<PagedResponse<CatDto>> getCatsByAuthorAsync(Long userId, String username, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats")
                        .queryParam(USER_ID_PARAM, userId)
                        .queryParam("username", username) // Добавлен параметр
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponse<CatDto>>() {})
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<CatDto> getCatByChatIdAsync(Long catId, String username) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/{id}")
                        .queryParam("username", username)
                        .build(catId))
                .retrieve()
                .bodyToMono(CatDto.class)
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<CatWithoutAuthorNameDto> getRandomCatAsync(
            Long userId
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/random")
                        .queryParam(USER_ID_PARAM, userId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка получения случайного котика: " + response.statusCode() + " - " + error
                                ))))
                .bodyToMono(CatWithoutAuthorNameDto.class)
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }


    @Async("asyncTaskExecutor")
    public CompletableFuture<Void> updateReactionAsync(Long catId, Long userId, ReactionType type) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/cats/{id}/reaction")
                        .queryParam(USER_ID_PARAM, userId)
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

    public CompletableFuture<String> uploadFileAsync(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                return webClient.post()
                        .uri("/upload")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData("file",
                                new ByteArrayResource(fileBytes) {
                                    @Override
                                    public String getFilename() {
                                        return "cat-photo.jpg";
                                    }
                                }))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response ->
                                response.bodyToMono(String.class)
                                        .flatMap(error -> Mono.error(new ServiceException(
                                                "Ошибка загрузки файла: " + response.statusCode() + " - " + error
                                        )))
                        )
                        .bodyToMono(String.class)
                        .block();
            } catch (IOException e) {
                throw new ServiceException("Ошибка чтения файла", e);
            }
        });
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Resource> getFileAsync(String filename) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/files/{filename}")
                        .build(filename))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Ошибка получения файла: " + response.statusCode() + " - " + error
                                )))
                )
                .bodyToMono(byte[].class)
                .map(bytes -> (Resource) new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                })
                .timeout(Duration.ofSeconds(10))
                .toFuture();
    }


}
