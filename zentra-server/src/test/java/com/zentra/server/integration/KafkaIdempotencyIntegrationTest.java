package com.zentra.server.integration;

import com.zentra.server.event.OrderCreatedEvent;
import com.zentra.server.event.OrderCreatedEventHandler;
import com.zentra.server.service.KafkaIdempotencyService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class KafkaIdempotencyIntegrationTest extends IntegrationTestBase {

    private static final String TOPIC = "zentra.order.created";

    private static final String DLT_TOPIC = TOPIC + ".DLT";

    private static final String GROUP_ID =
            "zentra-idempotency-test-" + UUID.randomUUID();

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

    @Autowired
    private KafkaIdempotencyService idempotencyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private OrderCreatedEventHandler eventHandler;

    @Test
    void shouldRejectDuplicateEventId() {

        String eventId =
                "integration-test-" + UUID.randomUUID();

        boolean firstResult =
                idempotencyService.tryMarkProcessed(eventId);

        boolean secondResult =
                idempotencyService.tryMarkProcessed(eventId);

        assertThat(firstResult)
                .isTrue();

        assertThat(secondResult)
                .isFalse();

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM processed_event
                        WHERE event_id = ?
                        """,
                        Integer.class,
                        eventId
                );

        assertThat(count)
                .isEqualTo(1);
    }

    @Test
    void shouldProcessDuplicateKafkaEventOnlyOnce()
            throws Exception {

        String eventId =
                "kafka-idempotency-" + UUID.randomUUID();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        eventId,
                        777777L,
                        1L,
                        1L
                );

        var firstResult =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        var secondResult =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        assertThat(firstResult.getRecordMetadata().partition())
                .isEqualTo(
                        secondResult.getRecordMetadata().partition()
                );

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        firstResult.getRecordMetadata().partition()
                );

        long expectedOffset =
                secondResult.getRecordMetadata().offset() + 1;

        waitForCommittedOffset(
                topicPartition,
                expectedOffset
        );

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM processed_event
                        WHERE event_id = ?
                        """,
                        Integer.class,
                        eventId
                );

        assertThat(count)
                .isEqualTo(1);
    }

    @Test
    void shouldMarkEventAsProcessedAfterSuccessfulRetry() throws Exception {
        String eventId =
                "kafka-retry-" + UUID.randomUUID();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        eventId,
                        888888L,
                        1L,
                        1L
                );

        doThrow(new RuntimeException("business failure"))
                .doNothing()
                .when(eventHandler)
                .handle(any(OrderCreatedEvent.class));

        var result =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        result.getRecordMetadata().partition()
                );

        waitForCommittedOffset(
                topicPartition,
                result.getRecordMetadata().offset() + 1
        );

        verify(eventHandler, times(2))
                .handle(any(OrderCreatedEvent.class));

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM processed_event
                        WHERE event_id = ?
                        """,
                        Integer.class,
                        eventId
                );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCommitOffsetAfterRetriesAreExhausted() throws Exception {
        String eventId =
                "kafka-final-failure-" + UUID.randomUUID();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        eventId,
                        999999L,
                        1L,
                        1L
                );

        doThrow(new RuntimeException("permanent business failure"))
                .when(eventHandler)
                .handle(any(OrderCreatedEvent.class));

        var result =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        result.getRecordMetadata().partition()
                );

        Thread.sleep(6_000);

        try (AdminClient adminClient = createAdminClient()) {
            Map<TopicPartition, OffsetAndMetadata> offsets =
                    adminClient
                            .listConsumerGroupOffsets(GROUP_ID)
                            .partitionsToOffsetAndMetadata()
                            .get(5, TimeUnit.SECONDS);

            OffsetAndMetadata committed =
                    offsets.get(topicPartition);

            assertThat(committed).isNotNull();

            assertThat(committed.offset())
                    .isEqualTo(
                            result.getRecordMetadata().offset() + 1
                    );
        }

        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM processed_event
                        WHERE event_id = ?
                        """,
                        Integer.class,
                        eventId
                );

        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldPublishFailedEventToDlt() throws Exception {
        String eventId =
                "kafka-dlt-" + UUID.randomUUID();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        eventId,
                        111111L,
                        1L,
                        1L
                );

        doThrow(new RuntimeException("permanent business failure"))
                .when(eventHandler)
                .handle(any(OrderCreatedEvent.class));

        var result =
                kafkaTemplate
                        .send(TOPIC, eventId, event)
                        .get(10, TimeUnit.SECONDS);

        TopicPartition topicPartition =
                new TopicPartition(
                        TOPIC,
                        result.getRecordMetadata().partition()
                );

        waitForCommittedOffset(
                topicPartition,
                result.getRecordMetadata().offset() + 1
        );

        try (var consumer =
                     createKafkaConsumer(
                             "zentra-dlt-test-" + UUID.randomUUID())) {

            consumer.subscribe(java.util.List.of(DLT_TOPIC));

            var deadline =
                    System.currentTimeMillis() + 10_000;

            ConsumerRecord<String, OrderCreatedEvent>
                    dltRecord = null;

            while (System.currentTimeMillis() < deadline) {

                var records =
                        consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, OrderCreatedEvent> record : records) {

                    if (eventId.equals(record.value().eventId())) {
                        dltRecord = record;
                        break;
                    }
                }

                if (dltRecord != null) {
                    break;
                }
            }

            assertThat(dltRecord).isNotNull();

            OrderCreatedEvent dltEvent =
                    dltRecord.value();

            assertThat(dltEvent.eventId())
                    .isEqualTo(eventId);

            assertThat(dltEvent.orderId())
                    .isEqualTo(111111L);

            assertThat(dltEvent.merchantId())
                    .isEqualTo(1L);

            assertThat(dltEvent.userId())
                    .isEqualTo(1L);

            assertThat(dltRecord.headers()
                    .lastHeader(KafkaHeaders.DLT_ORIGINAL_TOPIC))
                    .isNotNull();

            assertThat(new String(
                    dltRecord.headers()
                            .lastHeader(KafkaHeaders.DLT_ORIGINAL_TOPIC)
                            .value(),
                    StandardCharsets.UTF_8
            )).isEqualTo(TOPIC);

            assertThat(dltRecord.headers()
                    .lastHeader(KafkaHeaders.DLT_ORIGINAL_PARTITION))
                    .isNotNull();

            assertThat(ByteBuffer.wrap(
                    dltRecord.headers()
                            .lastHeader(KafkaHeaders.DLT_ORIGINAL_PARTITION)
                            .value()
            ).getInt()).isEqualTo(
                    result.getRecordMetadata().partition()
            );

            assertThat(dltRecord.headers()
                    .lastHeader(KafkaHeaders.DLT_ORIGINAL_OFFSET))
                    .isNotNull();

            assertThat(ByteBuffer.wrap(
                    dltRecord.headers()
                            .lastHeader(KafkaHeaders.DLT_ORIGINAL_OFFSET)
                            .value()
            ).getLong()).isEqualTo(
                    result.getRecordMetadata().offset()
            );
        }
    }

    private void waitForCommittedOffset(
            TopicPartition topicPartition,
            long expectedOffset
    ) throws Exception {

        try (AdminClient adminClient = createAdminClient()) {

            long deadline =
                    System.currentTimeMillis() + 10_000;

            while (System.currentTimeMillis() < deadline) {

                Map<TopicPartition, OffsetAndMetadata>
                        offsets =
                        adminClient
                                .listConsumerGroupOffsets(GROUP_ID)
                                .partitionsToOffsetAndMetadata()
                                .get(5, TimeUnit.SECONDS);

                var committed =
                        offsets.get(topicPartition);

                if (committed != null
                        && committed.offset() >= expectedOffset) {

                    return;
                }

                Thread.sleep(100);
            }
        }

        throw new AssertionError(
                "Kafka consumer did not commit expected offset: "
                        + expectedOffset
        );
    }

    private KafkaConsumer<String, OrderCreatedEvent>
    createKafkaConsumer(String groupId) {

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
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        properties.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                OrderCreatedEvent.class.getName()
        );

        properties.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.zentra.server.event"
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        return new KafkaConsumer<>(properties);
    }

    private AdminClient createAdminClient() {

        Properties properties =
                new Properties();

        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        return AdminClient.create(properties);
    }
}