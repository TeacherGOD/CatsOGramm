package com.example.catphototg.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSender {

    private final String countCat;
    private final String catsPage;
    private final String catDetails;
    private final String catRandom;


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCountCat(CatsMenuDto data) {
        kafkaTemplate.send(countCat, data);
    }
    public void sendCatsPage(PagedResponseMy<CatDto> data) {
        kafkaTemplate.send(catsPage, data);
    }
    public void sendCatDetailsMyCat(CatDetailsKafka data) {
        kafkaTemplate.send(catDetails, data);
    }
    public void sendCatDetailsRandomCat(CatDetailsKafka data) {
        kafkaTemplate.send(catRandom, data);
    }
}
