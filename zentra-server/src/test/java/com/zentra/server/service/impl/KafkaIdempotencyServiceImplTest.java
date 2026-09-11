package com.zentra.server.service.impl;

import com.zentra.server.entity.ProcessedEvent;
import com.zentra.server.mapper.ProcessedEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaIdempotencyServiceImplTest {

    @Mock
    private ProcessedEventMapper processedEventMapper;

    private KafkaIdempotencyServiceImpl service;

    @BeforeEach
    void setUp() {
        service =
                new KafkaIdempotencyServiceImpl(
                        processedEventMapper
                );
    }

    @Test
    void shouldMarkNewEventAsProcessed() {

        when(processedEventMapper.insert(any(ProcessedEvent.class)))
                .thenReturn(1);

        boolean result =
                service.tryMarkProcessed("event-001");

        assertThat(result)
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenEventAlreadyProcessed() {

        when(processedEventMapper.insert(any(ProcessedEvent.class)))
                .thenThrow(new DuplicateKeyException("duplicate"));

        boolean result =
                service.tryMarkProcessed("event-001");

        assertThat(result)
                .isFalse();
    }
}