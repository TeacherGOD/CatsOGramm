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
    @Value("${spring.kafka.server.producer.group.count}")
    private String countCatTopic;
    @Value("${spring.kafka.server.producer.topic.add}")
    private String addCatTopic;
    @Value("${spring.kafka.server.producer.topic.getMy}")
    private String getAllMyCatTopic;
    @Value("${spring.kafka.server.producer.topic.info}")
    private String getDetailsTopic;
    @Value("${spring.kafka.server.producer.topic.random}")
    private String getRandomCatTopic;
    @Value("${spring.kafka.server.producer.topic.reaction}")
    private String makeReactionTopic;
    @Value("${spring.kafka.server.producer.topic.delete}")
    private String deleteCatTopic;


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
    public String countCatTopic() {
        return countCatTopic;
    }

    @Bean
    public String addCatTopic() {
        return addCatTopic;
    }

    @Bean
    public String getAllMyCatTopic() {
        return getAllMyCatTopic;
    }

    @Bean
    public String getDetailsTopic() {
        return getDetailsTopic;
    }

    @Bean
    public String getRandomCatTopic() {
        return getRandomCatTopic;
    }

    @Bean
    public String makeReactionTopic() {
        return makeReactionTopic;
    }

    @Bean
    public String deleteCatTopic() {
        return deleteCatTopic;
    }


    @Bean
    public NewTopic countCatTopicNewTopic() {
        return TopicBuilder.name(countCatTopic).build();
    }

    @Bean
    public NewTopic addCatTopicNewTopic() {
        return TopicBuilder.name(addCatTopic).build();
    }

    @Bean
    public NewTopic getAllMyCatTopicNewTopic() {
        return TopicBuilder.name(getAllMyCatTopic).build();
    }

    @Bean
    public NewTopic getDetailsTopicNewTopic() {
        return TopicBuilder.name(getDetailsTopic).build();
    }

    @Bean
    public NewTopic getRandomCatTopicNewTopic() {
        return TopicBuilder.name(getRandomCatTopic).build();
    }

    @Bean
    public NewTopic makeReactionTopicNewTopic() {
        return TopicBuilder.name(makeReactionTopic).build();
    }

    @Bean
    public NewTopic deleteCatTopicNewTopic() {
        return TopicBuilder.name(deleteCatTopic).build();
    }

}
