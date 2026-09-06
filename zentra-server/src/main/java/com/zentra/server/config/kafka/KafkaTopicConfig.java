package com.zentra.server.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    private static final int ORDER_CREATED_PARTITIONS = 3;
    private static final short ORDER_CREATED_REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(
                KafkaTopics.ORDER_CREATED,
                ORDER_CREATED_PARTITIONS,
                ORDER_CREATED_REPLICATION_FACTOR
        );
    }
}