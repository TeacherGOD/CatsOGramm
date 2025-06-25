package com.example.catphototg.catservice.service;

import com.example.catphototg.catservice.dto.CatCreationDto;
import com.example.catphototg.catservice.dto.CatDto;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .onStatus(HttpStatusCode::isError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new ServiceException(
                                    "Ошибка сервиса котиков: " + response.statusCode() + " - " + error
                            )));
                })
                .bodyToMono(CatDto.class)
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
                .bodyToMono(Integer.class)
                .toFuture();
    }
}
