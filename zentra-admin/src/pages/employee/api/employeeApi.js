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
 * Get employee by id
 */
export function getEmployeeById(id) {
    return httpClient.get(`/employee/${id}`);
}

/**
 * Create employee
 */
export function createEmployee(data) {
    return httpClient.post("/employee", data);
}

/**
 * Update employee
 */
export function updateEmployee(data) {
    return httpClient.patch("/employee", data);
}

/**
 * Delete employee
 */
export function deleteEmployee(id) {
    return httpClient.delete(`/employee/${id}`);
}