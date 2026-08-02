import httpClient from "../../../services/http/httpClient";

/**
 * Get dish page
 */
export function getDishPage(params) {

    return httpClient.get(
        "/dish",
        {
            params,
        }
    );

}

/**
 * Get enabled dish list
 */
export function getDishList(categoryId) {

    return httpClient.get(
        "/dish/list",
        {
            params: {
                categoryId,
            },
        }
    );

}

/**
 * Get dish detail
 */
export function getDishById(id) {

    return httpClient.get(
        `/dish/${id}`
    );

}

/**
 * Create dish
 */
export function createDish(data) {

    return httpClient.post(
        "/dish",
        data
    );

}

/**
 * Update dish
 */
export function updateDish(data) {

    return httpClient.patch(
        "/dish",
        data
    );

}

/**
 * Delete dish
 */
export function deleteDish(id) {

    return httpClient.delete(
        `/dish/${id}`
    );

}