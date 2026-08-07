package com.zentra.server.service.impl;

import com.zentra.common.context.AuthContext;
import com.zentra.server.dto.DashboardDTO;
import com.zentra.server.mapper.DashboardMapper;
import com.zentra.server.service.DashboardService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for dashboard statistics
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(
            DashboardMapper dashboardMapper
    ) {

        this.dashboardMapper = dashboardMapper;
    }

    /**
     * Get dashboard statistics
     */
    @Override
    public DashboardDTO statistics() {

        Long merchantId =
                AuthContext.getCurrentMerchantId();

        return dashboardMapper.statistics(
                merchantId
        );

    }

}