package com.zentra.server.mapper;

import com.zentra.server.entity.ProcessedEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessedEventMapper {

    int insert(ProcessedEvent processedEvent);
}