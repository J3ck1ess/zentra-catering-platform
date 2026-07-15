import axios from "axios";
import { message } from "antd";
import {
    getToken,
    removeToken,
} from "../auth/tokenService";

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

    (response) => {

        const result = response.data;

        const SUCCESS_CODE = 1;
        const INVALID_TOKEN_CODE = 40001;

        // Token invalid or expired
        if (result.code === INVALID_TOKEN_CODE) {

            removeToken();

            window.location.replace("/login");

            return Promise.reject(
                new Error(result.msg)
            );

        }

        // Other business errors
        if (result.code !== SUCCESS_CODE) {

            return Promise.reject(
                new Error(result.msg)
            );

        }

        return result.data;

    },

    (error) => {

        return Promise.reject(error);

    }

);

/**
 * Shared HTTP client
 */
export default httpClient;