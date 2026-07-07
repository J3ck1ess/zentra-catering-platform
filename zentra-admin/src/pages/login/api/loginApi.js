import httpClient from "../../../services/http/httpClient";

/**
 * Login
 */
export function login(data) {
    return httpClient.post("/employee/login", data);
}