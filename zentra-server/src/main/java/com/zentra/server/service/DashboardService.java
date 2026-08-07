package com.zentra.server.service;

import com.zentra.server.dto.DashboardDTO;

/**
 * Service for dashboard statistics
 */
public interface DashboardService {

    /**
     * Get dashboard statistics
     *
     * @return dashboard statistics
     */
    DashboardDTO statistics();

}