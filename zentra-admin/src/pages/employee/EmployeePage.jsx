import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";
import EmployeeSearchForm from "./components/EmployeeSearchForm";
import EmployeeTable from "./components/EmployeeTable";
import EmployeeEditModal from "./components/EmployeeEditModal";
import EmployeeCreateModal from "./components/EmployeeCreateModal";
import { useEffect, useState } from "react";
import { message } from "antd";
import {
    createEmployee,
    deleteEmployee,
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

    const [createOpen, setCreateOpen] = useState(false);

    /**
     * Load employee page
     */
    async function loadEmployeePage(searchQuery) {

        try {

            setLoading(true);

            const employeePage = await getEmployeePage(searchQuery);

            setEmployeePage(employeePage);

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
     * Handle employee deletion
     */
    async function handleDelete(id) {

        try {

            await deleteEmployee(id);

            message.success(
                "Employee deleted successfully."
            );

            await loadEmployeePage(query);

        } catch (error) {

            message.error(
                "Failed to delete employee."
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

    /**
     * Open employee create dialog
     */
    function handleCreate() {

        setCreateOpen(true);

    }

    /**
     * Close employee create dialog
     */
    function handleCloseCreate() {

        setCreateOpen(false);

    }

    /**
     * Handle employee creation
     */
    async function handleCreateEmployee(employee) {

        try {

            await createEmployee(employee);

            message.success(
                "Employee created successfully."
            );

            setCreateOpen(false);

            await loadEmployeePage(query);

        } catch (error) {

            message.error(
                "Failed to create employee."
            );

            throw error;

        }

    }

    function handleCloseEdit() {

        setEditingEmployee(null);

    }

    /**
     * Load first page on component mount
     */
    useEffect(() => {

        void loadEmployeePage(query);

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
                    onCreate={handleCreate}
                />

                <EmployeeTable
                    loading={loading}
                    employeePage={employeePage}
                    query={query}
                    onPageChange={handlePageChange}
                    onStatusChange={handleStatusChange}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                />

            </PageContainer>

            <EmployeeEditModal
                open={editingEmployee !== null}
                employee={editingEmployee}
                onCancel={handleCloseEdit}
                onSave={handleUpdate}
            />

            <EmployeeCreateModal
                open={createOpen}
                onCancel={handleCloseCreate}
                onSave={handleCreateEmployee}
            />

        </>
    );
}

export default EmployeePage;