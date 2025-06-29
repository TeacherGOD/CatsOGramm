package com.example.catphototg.kafka;

import com.example.common.dto.CatCreationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSender {

    private final String countCatTopic;
    private final String addCatTopic;
    private final String getAllMyCatTopic;
    private final String getDetailsTopic;
    private final String getRandomCatTopic;
    private final String makeReactionTopic;
    private final String deleteCatTopic;


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCountCatsAsk(Long authorId) {
        kafkaTemplate.send(countCatTopic, authorId);
    }

    public void sendAddCat(CatCreationDto data) {
        kafkaTemplate.send(addCatTopic, data);
    }

    public void sendGetAllMyCat(CatPageRequest data) {
        kafkaTemplate.send(getAllMyCatTopic, data);
    }

    public void sendCatDetails(CatDetailsKafka data) {
        kafkaTemplate.send(getDetailsTopic, data);
    }

    public void sendGetRandomCat(Long userId) {
        kafkaTemplate.send(getRandomCatTopic, userId);
    }

    public void sendMakeReaction(UserReaction data) {
        kafkaTemplate.send(makeReactionTopic, data);
    }

    public void sendDeleteCat(CatDto data) {
        kafkaTemplate.send(deleteCatTopic, data);
    }
}
