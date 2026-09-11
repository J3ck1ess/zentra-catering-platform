package com.zentra.server.integration;

import com.zentra.server.event.OrderCreatedEvent;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class KafkaOffsetIntegrationTest extends IntegrationTestBase {

    private static final String TOPIC = "zentra.order.created";

    private static final String GROUP_ID =
            "zentra-offset-test-" + UUID.randomUUID();

    @DynamicPropertySource
    static void kafkaProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.kafka.consumer.group-id",
                () -> GROUP_ID
        );

        registry.add(
                "spring.kafka.listener.auto-startup",
                () -> true
        );
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Test
    void shouldCommitOffsetAfterSuccessfulConsumption() throws Exception {

        String eventId = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId,
                999999L,
                1L,
                1L
        );

        var result = kafkaTemplate
                .send(TOPIC, eventId, event)
                .get(10, TimeUnit.SECONDS);

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        result.getRecordMetadata().partition()
                );

        long expectedOffset =
                result.getRecordMetadata().offset() + 1;

        try (AdminClient adminClient = createAdminClient()) {

            long committedOffset =
                    waitForCommittedOffset(
                            adminClient,
                            topicPartition,
                            expectedOffset
                    );

            assertThat(committedOffset)
                    .isEqualTo(expectedOffset);
        }
    }

    @Test
    void shouldConsumeAgainWhenConsumerStopsBeforeOffsetCommit()
            throws Exception {

        String testGroupId =
                "kafka-offset-crash-test-" + UUID.randomUUID();

        String eventId =
                UUID.randomUUID().toString();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        eventId,
                        888888L,
                        1L,
                        1L
                );

        var result =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        result.getRecordMetadata().partition()
                );

        long messageOffset =
                result.getRecordMetadata().offset();

        Properties properties =
                createConsumerProperties(testGroupId);

        // First consumer
        try (KafkaConsumer<String, String> consumer =
                     new KafkaConsumer<>(properties)) {

            consumer.subscribe(List.of(TOPIC));

            ConsumerRecord<String, String> record =
                    waitForRecord(
                            consumer,
                            topicPartition,
                            messageOffset
                    );

            assertThat(record)
                    .isNotNull();

            System.out.println(
                    "[KAFKA TEST] First consumption completed. " +
                            "eventId=" + eventId +
                            ", offset=" + record.offset()
            );

            // Simulate successful business processing.
            //
            // Intentionally do NOT commit the offset.
        }

        /*
         * Verify that the consumed record was not committed.
         */
        try (AdminClient adminClient = createAdminClient()) {

            Map<TopicPartition, OffsetAndMetadata> offsets =
                    adminClient
                            .listConsumerGroupOffsets(testGroupId)
                            .partitionsToOffsetAndMetadata()
                            .get(5, TimeUnit.SECONDS);

            OffsetAndMetadata committed =
                    offsets.get(topicPartition);

            assertThat(committed)
                    .isNull();

            System.out.println(
                    "[KAFKA TEST] Offset was not committed. " +
                            "eventId=" + eventId +
                            ", offset=" + messageOffset
            );
        }

        // Restart consumer with the SAME consumer group.
        try (KafkaConsumer<String, String> restartedConsumer =
                     new KafkaConsumer<>(properties)) {

            restartedConsumer.subscribe(List.of(TOPIC));

            ConsumerRecord<String, String> redeliveredRecord =
                    waitForRecord(
                            restartedConsumer,
                            topicPartition,
                            messageOffset
                    );

            assertThat(redeliveredRecord)
                    .isNotNull();

            assertThat(redeliveredRecord.offset())
                    .isEqualTo(messageOffset);

            System.out.println(
                    "[KAFKA TEST] Redelivery confirmed. " +
                            "eventId=" + eventId +
                            ", offset=" +
                            redeliveredRecord.offset()
            );
        }
    }

    private AdminClient createAdminClient() {

        Properties properties = new Properties();

        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        return AdminClient.create(properties);
    }

    private long waitForCommittedOffset(
            AdminClient adminClient,
            TopicPartition topicPartition,
            long expectedOffset
    ) throws Exception {

        long deadline =
                System.currentTimeMillis() + 10_000;

        while (System.currentTimeMillis() < deadline) {

            Map<TopicPartition, OffsetAndMetadata> offsets =
                    adminClient
                            .listConsumerGroupOffsets(GROUP_ID)
                            .partitionsToOffsetAndMetadata()
                            .get(5, TimeUnit.SECONDS);

            OffsetAndMetadata committed =
                    offsets.get(topicPartition);

            if (committed != null
                    && committed.offset() >= expectedOffset) {

                return committed.offset();
            }

            Thread.sleep(100);
        }

        Map<TopicPartition, OffsetAndMetadata> offsets =
                adminClient
                        .listConsumerGroupOffsets(GROUP_ID)
                        .partitionsToOffsetAndMetadata()
                        .get(5, TimeUnit.SECONDS);

        OffsetAndMetadata committed =
                offsets.get(topicPartition);

        return committed == null
                ? -1
                : committed.offset();
    }

    private Properties createConsumerProperties(
            String groupId
    ) {

        Properties properties =
                new Properties();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId
        );

        properties.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        return properties;
    }

    private ConsumerRecord<String, String> waitForRecord(
            KafkaConsumer<String, String> consumer,
            TopicPartition targetPartition,
            long expectedOffset
    ) {

        long deadline =
                System.currentTimeMillis() + 10_000;

        while (System.currentTimeMillis() < deadline) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record :
                    records.records(targetPartition)) {

                if (record.offset() == expectedOffset) {
                    return record;
                }
            }
        }

        return null;
    }
}