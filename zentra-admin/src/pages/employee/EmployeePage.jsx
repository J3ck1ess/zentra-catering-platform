import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";
import EmployeeSearchForm from "./components/EmployeeSearchForm";
import EmployeeTable from "./components/EmployeeTable";
import EmployeeEditModal from "./components/EmployeeEditModal";
import { useEffect, useState } from "react";
import { message } from "antd";
import {
    getEmployeePage,
    updateEmployee,
    updateEmployeeStatus,
} from "./api/employeeApi";

/**
 * Employee management page
 */
function EmployeePage() {

    const [query, setQuery] = useState({
        page: 1,
        pageSize: 10,
    });

    const [employeePage, setEmployeePage] = useState(null);

    const [loading, setLoading] = useState(false);

    const [editingEmployee, setEditingEmployee] = useState(null);

    /**
     * Load employee page
     */
    async function loadEmployeePage(searchQuery) {

        try {

            setLoading(true);

            const response = await getEmployeePage(searchQuery);

            setEmployeePage(response.data.data);

        } finally {

            setLoading(false);

        }

    }

    /**
     * Handle employee search
     */
    function handleSearch(searchQuery) {

        const nextQuery = {
            ...query,
            ...searchQuery,
            page: 1,
        };

        setQuery(nextQuery);

        loadEmployeePage(nextQuery);

    }

    /**
     * Handle page change
     */
    function handlePageChange(page, pageSize) {

        const nextQuery = {
            ...query,
            page,
            pageSize,
        };

        setQuery(nextQuery);

        loadEmployeePage(nextQuery);

    }

    /**
     * Handle employee status update
     */
    async function handleStatusChange(id, checked) {

        const status = checked ? 1 : 0;

        try {

            await updateEmployeeStatus({
                id,
                status,
            });

            message.success(
                "Employee status updated successfully."
            );

            await loadEmployeePage(query);

        } catch (error) {

            message.error(
                "Failed to update employee status."
            );

        }

    }

    /**
     * Handle employee update
     */
    async function handleUpdate(employee) {

        try {

            await updateEmployee(employee);

            message.success(
                "Employee updated successfully."
            );

            setEditingEmployee(null);

            await loadEmployeePage(query);

        } catch (error) {

            message.error(
                "Failed to update employee."
            );

            throw error;

        }

    }

    /**
     * Open employee edit dialog
     */
    function handleEdit(employee) {

        setEditingEmployee(employee);

    }

    function handleCloseEdit() {

        setEditingEmployee(null);

    }

    /**
     * Load first page on component mount
     */
    useEffect(() => {

        loadEmployeePage(query);

    }, []);

    return (
        <>

            <PageHeader
                title="Employee Management"
                description="Manage employee accounts and permissions"
            />

            <PageContainer>

                <EmployeeSearchForm
                    onSearch={handleSearch}
                />

                <EmployeeTable
                    loading={loading}
                    employeePage={employeePage}
                    query={query}
                    onPageChange={handlePageChange}
                    onStatusChange={handleStatusChange}
                    onEdit={handleEdit}
                />

                Pagination

            </PageContainer>

            <EmployeeEditModal
                open={editingEmployee !== null}
                employee={editingEmployee}
                onCancel={handleCloseEdit}
                onSave={handleUpdate}
            />

        </>
    );
}

export default EmployeePage;