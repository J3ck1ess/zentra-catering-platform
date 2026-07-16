import { useEffect, useState } from "react";
import { message } from "antd";

import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";

import CategorySearchForm from "./components/CategorySearchForm";
import CategoryTable from "./components/CategoryTable";
import CategoryCreateModal from "./components/CategoryCreateModal";
import CategoryEditModal from "./components/CategoryEditModal";

import {
    createCategory,
    deleteCategory,
    getCategoryPage,
    updateCategory,
} from "./api/categoryApi";

/**
 * Category management page
 */
function CategoryPage() {

    const [query, setQuery] = useState({
        page: 1,
        pageSize: 10,
    });

    const [categoryPage, setCategoryPage] = useState(null);

    const [loading, setLoading] = useState(false);

    const [editingCategory, setEditingCategory] = useState(null);

    const [createOpen, setCreateOpen] = useState(false);

    /**
     * Load category page
     */
    async function loadCategoryPage(searchQuery) {

        try {

            setLoading(true);

            const categoryPage = await getCategoryPage(searchQuery);

            setCategoryPage(categoryPage);

        } finally {

            setLoading(false);

        }

    }

    function handleSearch(searchQuery) {

        const nextQuery = {
            ...query,
            ...searchQuery,
            page: 1,
        };

        setQuery(nextQuery);

        void loadCategoryPage(nextQuery);

    }

    function handlePageChange(page, pageSize) {

        const nextQuery = {
            ...query,
            page,
            pageSize,
        };

        setQuery(nextQuery);

        void loadCategoryPage(nextQuery);

    }

    function handleCreate() {

        setCreateOpen(true);

    }

    function handleCloseCreate() {

        setCreateOpen(false);

    }

    async function handleCreateCategory(category) {

        try {

            await createCategory({
                ...category,
                type: 1,
                sort: 0,
            });

            message.success(
                "Category created successfully."
            );

            setCreateOpen(false);

            await loadCategoryPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to create category."
            );

            throw error;

        }

    }

    function handleEdit(category) {

        setEditingCategory(category);

    }

    function handleCloseEdit() {

        setEditingCategory(null);

    }

    async function handleUpdate(category) {

        try {

            await updateCategory({
                ...category,
                type: 1,
            });

            message.success(
                "Category updated successfully."
            );

            setEditingCategory(null);

            await loadCategoryPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to update category."
            );

            throw error;

        }

    }

    async function handleDelete(id) {

        try {

            await deleteCategory(id);

            message.success(
                "Category deleted successfully."
            );

            await loadCategoryPage(query);

        } catch (error) {

            message.error(
                error.message ?? "Failed to delete category."
            );

            throw error;

        }

    }

    useEffect(() => {

        void loadCategoryPage(query);

    }, []);

    return (
        <>

            <PageHeader
                title="Category Management"
                description="Manage dish categories"
            />

            <PageContainer>

                <CategorySearchForm
                    onSearch={handleSearch}
                    onCreate={handleCreate}
                />

                <CategoryTable
                    loading={loading}
                    categoryPage={categoryPage}
                    query={query}
                    onPageChange={handlePageChange}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                />

            </PageContainer>

            <CategoryEditModal
                open={editingCategory !== null}
                category={editingCategory}
                onCancel={handleCloseEdit}
                onSave={handleUpdate}
            />

            <CategoryCreateModal
                open={createOpen}
                onCancel={handleCloseCreate}
                onSave={handleCreateCategory}
            />

        </>
    );
}

export default CategoryPage;