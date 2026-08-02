import { useEffect, useState } from "react";
import { message } from "antd";

import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";

import DishSearchForm from "./components/DishSearchForm";
import DishTable from "./components/DishTable";
import DishCreateModal from "./components/DishCreateModal";
import DishEditModal from "./components/DishEditModal";

import {
    createDish,
    deleteDish,
    getDishPage,
    updateDish,
} from "./api/dishApi";

import { getCategoryList } from "../category/api/categoryApi";

/**
 * Dish management page
 */
function DishPage() {

    const [query, setQuery] = useState({
        page: 1,
        pageSize: 10,
    });

    const [dishPage, setDishPage] = useState(null);

    const [categories, setCategories] = useState([]);

    const [loading, setLoading] = useState(false);

    const [editingDish, setEditingDish] = useState(null);

    const [createOpen, setCreateOpen] = useState(false);

    /**
     * Load dish page
     */
    async function loadDishPage(searchQuery) {

        try {

            setLoading(true);

            const dishPage = await getDishPage(searchQuery);

            setDishPage(dishPage);

        } finally {

            setLoading(false);

        }

    }

    /**
     * Load category list
     */
    async function loadCategories() {

        const categories = await getCategoryList();

        setCategories(categories);

    }

    /**
     * Search
     */
    function handleSearch(searchQuery) {

        const nextQuery = {
            ...query,
            ...searchQuery,
            page: 1,
        };

        setQuery(nextQuery);

        void loadDishPage(nextQuery);

    }

    /**
     * Pagination
     */
    function handlePageChange(page, pageSize) {

        const nextQuery = {
            ...query,
            page,
            pageSize,
        };

        setQuery(nextQuery);

        void loadDishPage(nextQuery);

    }

    /**
     * Open create dialog
     */
    function handleCreate() {

        setCreateOpen(true);

    }

    /**
     * Close create dialog
     */
    function handleCloseCreate() {

        setCreateOpen(false);

    }

    /**
     * Create dish
     */
    async function handleCreateDish(dish) {

        try {

            await createDish(dish);

            message.success(
                "Dish created successfully."
            );

            setCreateOpen(false);

            await loadDishPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to create dish."
            );

            throw error;

        }

    }

    /**
     * Open edit dialog
     */
    function handleEdit(dish) {

        setEditingDish(dish);

    }

    /**
     * Close edit dialog
     */
    function handleCloseEdit() {

        setEditingDish(null);

    }

    /**
     * Update dish
     */
    async function handleUpdate(dish) {

        try {

            await updateDish(dish);

            message.success(
                "Dish updated successfully."
            );

            setEditingDish(null);

            await loadDishPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to update dish."
            );

            throw error;

        }

    }

    /**
     * Update dish status
     */
    async function handleStatusChange(id, status) {

        try {

            await updateDish({
                id,
                status,
            });

            message.success(
                "Dish status updated successfully."
            );

            await loadDishPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to update dish status."
            );

        }

    }

    /**
     * Delete dish
     */
    async function handleDelete(id) {

        try {

            await deleteDish(id);

            message.success(
                "Dish deleted successfully."
            );

            await loadDishPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to delete dish."
            );

            throw error;

        }

    }

    useEffect(() => {

        void loadDishPage(query);

        void loadCategories();

    }, []);

    return (
        <>

            <PageHeader
                title="Dish Management"
                description="Manage dishes"
            />

            <PageContainer>

                <DishSearchForm
                    categories={categories}
                    onSearch={handleSearch}
                    onCreate={handleCreate}
                />

                <DishTable
                    loading={loading}
                    dishPage={dishPage}
                    query={query}
                    onPageChange={handlePageChange}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onStatusChange={handleStatusChange}
                />

            </PageContainer>

            <DishEditModal
                open={editingDish !== null}
                dish={editingDish}
                categories={categories}
                onCancel={handleCloseEdit}
                onSave={handleUpdate}
            />

            <DishCreateModal
                open={createOpen}
                categories={categories}
                onCancel={handleCloseCreate}
                onSave={handleCreateDish}
            />

        </>

    );

}

export default DishPage;