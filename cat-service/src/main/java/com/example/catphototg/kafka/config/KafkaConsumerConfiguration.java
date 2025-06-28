package com.example.catphototg.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.server.consumer.group.id}")
    private String consumerGroupId;
//    @Value("${spring.kafka.server.consumer.group.count}")
//    private String countCatTopic;
//    @Value("${spring.kafka.server.consumer.topic.add}")
//    private String addCatTopic;
//    @Value("${spring.kafka.server.consumer.topic.getMy}")
//    private String getAllMyCatTopic;
//    @Value("${spring.kafka.server.consumer.topic.info}")
//    private String getDetailsTopic;
//    @Value("${spring.kafka.server.consumer.topic.random}")
//    private String getRandomCatTopic;
//    @Value("${spring.kafka.server.consumer.topic.reaction}")
//    private String makeReactionTopic;
//    @Value("${spring.kafka.server.consumer.topic.delete}")
//    private String deleteCatTopic;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public List<String> kafkaTopics(
            @Value("${spring.kafka.server.consumer.topic.add}") String addCatTopic,
            @Value("${spring.kafka.server.consumer.topic.getMy}") String getAllMyCatTopic,
            @Value("${spring.kafka.server.consumer.topic.info}") String getDetailsTopic,
            @Value("${spring.kafka.server.consumer.topic.random}") String getRandomCatTopic,
            @Value("${spring.kafka.server.consumer.topic.reaction}") String makeReactionTopic,
            @Value("${spring.kafka.server.consumer.topic.delete}") String deleteCatTopic,
            @Value("${get.count}") String countCatTopic
    ) {
        return Arrays.asList(
                addCatTopic,
                getAllMyCatTopic,
                getDetailsTopic,
                getRandomCatTopic,
                makeReactionTopic,
                deleteCatTopic,
                countCatTopic
        );
    }


    @Bean
    public String serverGroupId() {
        return consumerGroupId;
    }



}
