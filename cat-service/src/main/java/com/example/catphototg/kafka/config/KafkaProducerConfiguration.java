package com.example.catphototg.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.server.producer.topic.count}")
    private String countCat;
    @Value("${spring.kafka.server.producer.topic.catsPage}")
    private String catsPage;
    @Value("${spring.kafka.server.producer.topic.cat.details}")
    private String catDetails;
    @Value("${spring.kafka.server.producer.topic.cat.details.random}")
    private String catRandom;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        var res =new KafkaTemplate<>(producerFactory());
        return res;
    }

    @Bean
    public String countCat() {
        return countCat;
    }

    @Bean
    public NewTopic producerTopic() {
        return TopicBuilder.name(countCat).build();
    }
    @Bean
    public String catsPage() {
        return catsPage;
    }

    @Bean
    public NewTopic producerTopic2() {
        return TopicBuilder.name(catsPage).build();
    }
    @Bean
    public NewTopic producerTopic3() {
        return TopicBuilder.name(catDetails).build();
    }
    @Bean
    public String catDetails() {
        return catDetails;
    }
    @Bean
    public NewTopic producerTopic4() {
        return TopicBuilder.name(catRandom).build();
    }
    @Bean
    public String catRandom() {
        return catRandom;
    }

}
