import httpClient from "../../../services/http/httpClient";

/**
 * Get dashboard statistics
 */
export function getDashboardStatistics() {

    return httpClient.get(
        "/dashboard"
    );

}