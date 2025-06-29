package com.example.catphototg.kafka;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.mapper.CatMapper;
import com.example.catphototg.service.CatService;
import com.example.catphototg.service.RandomCatService;
import com.example.common.dto.CatCreationDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @PostConstruct
    public void onPostConstruct() {
        log.info("init consumer");
    }


    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.add}", groupId = "#{@serverGroupId}")
    public void onAddCat(CatCreationDto dto) {
        catService.saveCat(dto);
        //log.info("Получено сообщение на добавление кота: {}", data);
        // обработка добавления кота
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.getMy}", groupId = "#{@serverGroupId}")
    public void onGetMyCats(CatPageRequest catPageRequest) {
        catService.getCatsCountByAuthor(catPageRequest.userId());
        Page<CatDto> pageResult =catService.getCatsByAuthor(catPageRequest.userId(), catPageRequest.page(), catPageRequest.size())
                .map(cat -> catMapper.toDto(cat, catPageRequest.username(),catPageRequest.chatId()));
        kafkaSender.sendCatsPage( new PagedResponseMy<CatDto>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                catPageRequest.chatId()
        ));
        log.info("Получено сообщение на получение всех моих котов: {}", catPageRequest.userId());
        // обработка получения всех котов пользователя
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.info}", groupId = "#{@serverGroupId}")
    public void onGetCatDetails(CatDetailsKafka catDto) {
        Cat cat = catService.getCatById(catDto.catId());
        kafkaSender.sendCatDetailsMyCat(new CatDetailsKafka(
                catDto.telegramId(),
                cat.getId(),
                cat.getName(),
                cat.getFilePath(),
                catDto.chatId(),
                catDto.userId()
                ));

        log.info("Получено сообщение на получение информации о коте: {}", catDto.catId());
        // обработка получения информации о коте
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.random}", groupId = "#{@serverGroupId}")
    public void onGetRandomCat(CatDetailsKafka catDto) {
        var res = randomCatService.getRandomCatForUserId(catDto.userId())
                .orElseThrow(() -> new CatNotFoundException("No unseen cats available"));
        CatDetailsKafka dto = new CatDetailsKafka(
                catDto.telegramId(),
                res.id(),
                res.name(),
                res.filePath(),
                catDto.chatId(),
                res.authorId()
        );

        kafkaSender.sendCatDetailsRandomCat(dto);

        // обработка получения случайного кота
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.reaction}", groupId = "#{@serverGroupId}")
    public void onMakeReaction(UserReaction userReaction) {
        randomCatService.processReaction(userReaction.userId(), userReaction.catId(), userReaction.type());
        //log.info("Получено сообщение на реакцию: {}", data);
        // обработка реакции на кота
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.topic.delete}", groupId = "#{@serverGroupId}")
    public void onDeleteCat(CatDto catDto) {
        catService.deleteCatById(catDto.id(), catDto.authorId());
        //log.info("Получено сообщение на удаление кота: {}", data);
        // обработка удаления кота
    }

    @KafkaListener(topics = "${spring.kafka.server.consumer.group.count}", groupId = "#{@serverGroupId}")
    public void onCountCats(CatsMenuDto catsMenuDto) {
        var countsCat=catService.getCatsCountByAuthor(catsMenuDto.userId());

        kafkaSender.sendCountCat(new CatsMenuDto(
                catsMenuDto.chatId(),
                catsMenuDto.userId(),
                catsMenuDto.telegramId(),
                catsMenuDto.username(),
                countsCat
        ));
        //log.info("Получено сообщение на подсчёт котов: {}", data);
        // обработка подсчёта котов
    }

}
