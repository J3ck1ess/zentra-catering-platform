import httpClient from "../../../services/http/httpClient";

/**
 * Get category page
 */
export function getCategoryPage(params) {

    return httpClient.get(
        "/category",
        {
            params,
        }
    );

}

/**
 * Get category list
 */
export function getCategoryList() {

    return httpClient.get(
        "/category/list"
    );

}

/**
 * Create category
 */
export function createCategory(data) {

    return httpClient.post(
        "/category",
        data
    );

}

/**
 * Update category
 */
export function updateCategory(data) {

    return httpClient.patch(
        "/category",
        data
    );

}

/**
 * Delete category
 */
export function deleteCategory(id) {

    return httpClient.delete(
        `/category/${id}`
    );

}