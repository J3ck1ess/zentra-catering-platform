package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessedEvent {

    private Long id;

    private String eventId;

    private LocalDateTime processedAt;
}