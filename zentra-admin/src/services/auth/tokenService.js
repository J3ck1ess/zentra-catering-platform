const TOKEN_KEY = "zentra_token";

/**
 * Get access token
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * Save access token
 */
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

/**
 * Remove access token
 */
export function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}