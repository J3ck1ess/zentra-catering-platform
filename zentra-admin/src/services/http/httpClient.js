import axios from "axios";
import { getToken } from "../auth/tokenService";

/**
 * Shared HTTP client
 */
const httpClient = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

/**
 * Request interceptor
 */
httpClient.interceptors.request.use(
    (config) => {
        const token = getToken();

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

/**
 * Response interceptor
 */
httpClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            console.error("Unauthorized");
        }

        return Promise.reject(error);
    }
);

/**
 * Shared HTTP client
 */
export default httpClient;