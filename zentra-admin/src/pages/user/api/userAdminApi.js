import httpClient from "../../../services/http/httpClient";

/**
 * Get user page
 */
export function getUserPage(params) {

    return httpClient.get(
        "/admin/users",
        {
            params,
        }
    );

}

/**
 * Update user status
 */
export function updateUserStatus(id, status) {

    return httpClient.patch(
        `/admin/users/${id}/status`,
        {
            status,
        }
    );

}