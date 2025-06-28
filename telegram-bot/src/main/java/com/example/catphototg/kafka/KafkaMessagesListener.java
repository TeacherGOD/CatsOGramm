package com.example.catphototg.kafka;

import com.example.catphototg.handlers.ViewCatsHandler;
import com.example.catphototg.mapper.CatMapper;
import com.example.catphototg.service.CatCardService;
import com.example.catphototg.service.CatService;
import com.example.catphototg.service.NavigationService;
import com.example.catphototg.service.RandomCatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessagesListener {
    private final CatService catService;
    private final CatMapper catMapper;
    private final RandomCatService randomCatService;
    private final KafkaSender kafkaSender;
    private final NavigationService navigationService;
    private final CatCardService catCardService;
    private final ViewCatsHandler viewCatsHandler;

    @PostConstruct
    public void onPostConstruct() {
        log.info("init consumer");
    }


    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.getMy}", groupId = "#{@serverGroupId}")
    public void onGetMyCats(PagedResponseMy<CatDto> pagedResponseMy) {
        navigationService.showCatsPage(pagedResponseMy);
        log.info("Получено сообщение на получение всех моих котов: {}", pagedResponseMy.content());
        // обработка получения всех котов пользователя
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.cat.details}", groupId = "#{@serverGroupId}")
    public void onGetCatDetails(CatDetailsKafka catDto) {
        catCardService.showCatCard(catDto);

        log.info("Получено сообщение на получение информации о my коте: {}", catDto.catId());
        // обработка получения информации о коте
    }
    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.cat.details.random}", groupId = "#{@serverGroupId}")
    public void onGetCatDetailsRandom(CatDto catDto) {
        viewCatsHandler.showRandomCat(catDto);

        log.info("Получено сообщение на получение информации о random коте: {}", catDto.id());
        // обработка получения информации о коте
    }


    @KafkaListener(topics = "${spring.kafka.server.consumer.group.count}", groupId = "#{@serverGroupId}")
    public void onCountCats(CatsMenuDto dto) {
        catCardService.updateUIAfterDeletion(dto.userId(),dto.chatId(),dto.telegramId(),dto.username(),dto.count());
        //log.info("Получено сообщение на подсчёт котов: {}", data);
        // обработка подсчёта котов
    }

}
