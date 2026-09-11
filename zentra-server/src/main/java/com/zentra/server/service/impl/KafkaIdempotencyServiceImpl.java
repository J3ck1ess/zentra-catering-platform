package com.zentra.server.service.impl;

import com.zentra.server.entity.ProcessedEvent;
import com.zentra.server.mapper.ProcessedEventMapper;
import com.zentra.server.service.KafkaIdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaIdempotencyServiceImpl
        implements KafkaIdempotencyService {

    private final ProcessedEventMapper processedEventMapper;

    @Override
    public boolean tryMarkProcessed(String eventId) {

        ProcessedEvent processedEvent =
                new ProcessedEvent();

        processedEvent.setEventId(eventId);

        try {

            processedEventMapper.insert(processedEvent);

            log.info(
                    "[KAFKA] Event marked as processed. eventId={}",
                    eventId
            );

            return true;

        } catch (DuplicateKeyException e) {

            log.info(
                    "[KAFKA] Duplicate event detected. eventId={}",
                    eventId
            );

            return false;
        }
    }
}