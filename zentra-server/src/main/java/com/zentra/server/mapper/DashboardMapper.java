package com.zentra.server.mapper;

import com.zentra.server.dto.DashboardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper for dashboard statistics
 */
@Mapper
public interface DashboardMapper {

    /**
     * Get dashboard statistics
     */
    DashboardDTO statistics(
            @Param("merchantId")
            Long merchantId
    );

}