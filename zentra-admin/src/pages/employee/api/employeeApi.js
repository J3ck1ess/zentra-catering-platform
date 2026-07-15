import httpClient from "../../../services/http/httpClient";

/**
 * Get employee page
 */
export function getEmployeePage(params) {
    return httpClient.get("/employee", {
        params,
    });
}

/**
 * Update employee status
 */
export function updateEmployeeStatus(data) {

    return httpClient.put(
        "/employee/status",
        data
    );

}

/**
 * Update employee
 */
export function updateEmployee(data) {

    return httpClient.patch(
        "/employee",
        data
    );

}

/**
 * Get employee by id
 */
export function getEmployeeById(id) {
    return httpClient.get(`/employee/${id}`);
}

/**
 * Get current login employee
 */
export function getCurrentEmployee() {

    return httpClient.get(
        "/employee/me"
    );

}

/**
 * Create employee
 */
export function createEmployee(data) {
    return httpClient.post("/employee", data);
}

/**
 * Delete employee
 */
export function deleteEmployee(id) {
    return httpClient.delete(`/employee/${id}`);
}