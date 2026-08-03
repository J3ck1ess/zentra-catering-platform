import { useEffect, useState } from "react";

import { message } from "antd";

import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";

import UserSearchForm from "./components/UserSearchForm";
import UserTable from "./components/UserTable";

import {
    getUserPage,
    updateUserStatus,
} from "./api/userAdminApi";

/**
 * User management page
 */
function UserPage() {

    const [query, setQuery] = useState({
        page: 1,
        pageSize: 10,
    });

    const [loading, setLoading] = useState(false);

    const [userPage, setUserPage] = useState(null);

    /**
     * Load user page
     */
    async function loadUserPage(searchQuery) {

        try {

            setLoading(true);

            const page = await getUserPage(searchQuery);

            setUserPage(page);

        } finally {

            setLoading(false);

        }

    }

    /**
     * Search users
     */
    function handleSearch(searchQuery) {

        const nextQuery = {
            ...query,
            ...searchQuery,
            page: 1,
        };

        setQuery(nextQuery);

        void loadUserPage(nextQuery);

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

        void loadUserPage(nextQuery);

    }

    /**
     * Update user status
     */
    async function handleStatusChange(id, status) {

        try {

            await updateUserStatus(
                id,
                status
            );

            message.success(
                "User status updated successfully."
            );

            await loadUserPage(query);

        } catch (error) {

            message.error(
                error.message ??
                "Failed to update user status."
            );

            throw error;

        }

    }

    useEffect(() => {

        void loadUserPage(query);

    }, []);

    return (

        <>

            <PageHeader
                title="User Management"
                description="Manage platform users"
            />

            <PageContainer>

                <UserSearchForm
                    onSearch={handleSearch}
                />

                <UserTable
                    loading={loading}
                    userPage={userPage}
                    query={query}
                    onPageChange={handlePageChange}
                    onStatusChange={handleStatusChange}
                />

            </PageContainer>

        </>

    );

}

export default UserPage;