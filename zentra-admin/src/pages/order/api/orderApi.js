import httpClient from "../../../services/http/httpClient";

/**
 * Get order page
 */
export function getOrderPage(params) {

    return httpClient.get(
        "/order",
        {
            params,
        }
    );

}

/**
 * Get order detail
 */
export function getOrderDetail(id) {

    return httpClient.get(
        `/order/${id}`
    );

}

/**
 * Update order status
 */
export function updateOrderStatus(id, status) {

    return httpClient.patch(
        `/order/${id}/status`,
        {
            status,
        }
    );

}