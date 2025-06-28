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
    @Value("${tg.bot.group.id}")
    private String consumerGroupId;


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
            @Value("${spring.kafka.server.consumer.topic.count}") String countCatAnswer,
            @Value("${spring.kafka.server.consumer.topic.catsPage}") String catsPage,
            @Value("${spring.kafka.server.consumer.topic.cat.details}") String catDetails,
            @Value("${spring.kafka.server.consumer.topic.cat.details.random}") String catDetailsRandom
    ) {
        return Arrays.asList(
                countCatAnswer,
                catsPage,
                catDetails,
                catDetailsRandom
        );
    }


    @Bean
    public String serverGroupId() {
        return consumerGroupId;
    }



}
