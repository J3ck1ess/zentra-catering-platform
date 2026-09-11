package com.zentra.server.integration;

import com.zentra.server.event.OrderCreatedEvent;
import com.zentra.server.event.OrderCreatedEventHandler;
import com.zentra.server.service.KafkaIdempotencyService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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

    // TODO: Current DefaultErrorHandler uses the default logging recoverer.
    //  After retry exhaustion, the failed record is recovered and its offset is committed without creating a persistent idempotency record.
    //  This behavior will be replaced by DLT/DLQ handling
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