package com.zentra.server.integration;

import com.zentra.server.entity.ProcessedEvent;
import com.zentra.server.mapper.ProcessedEventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessedEventMapperIntegrationTest
        extends IntegrationTestBase {

    @Autowired
    private ProcessedEventMapper processedEventMapper;

    @Test
    void shouldInsertProcessedEvent() {

        ProcessedEvent event =
                new ProcessedEvent();

        event.setEventId(
                "mapper-test-" + System.currentTimeMillis()
        );

        int affectedRows =
                processedEventMapper.insert(event);

        assertThat(affectedRows)
                .isEqualTo(1);

        assertThat(event.getId())
                .isNotNull();
    }

    @Test
    void shouldRejectDuplicateEventId() {

        String eventId =
                "duplicate-test-" + System.currentTimeMillis();

        ProcessedEvent first =
                new ProcessedEvent();

        first.setEventId(eventId);

        processedEventMapper.insert(first);

        ProcessedEvent duplicate =
                new ProcessedEvent();

        duplicate.setEventId(eventId);

        assertThatThrownBy(
                () -> processedEventMapper.insert(duplicate)
        )
                .isInstanceOf(DuplicateKeyException.class);
    }
}