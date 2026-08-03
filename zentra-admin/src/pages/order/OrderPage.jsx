import { useEffect, useState } from "react";

import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";

import OrderSearchForm from "./components/OrderSearchForm";
import OrderTable from "./components/OrderTable";
import OrderDetailModal from "./components/OrderDetailModal";

import {
    getOrderDetail,
    getOrderPage,
} from "./api/orderApi";

/**
 * Order management page
 */
function OrderPage() {

    const [query, setQuery] = useState({
        page: 1,
        pageSize: 10,
    });

    const [loading, setLoading] = useState(false);

    const [orderPage, setOrderPage] = useState(null);

    const [detailOpen, setDetailOpen] = useState(false);

    const [orderDetail, setOrderDetail] = useState(null);

    /**
     * Load order page
     */
    async function loadOrderPage(searchQuery) {

        try {

            setLoading(true);

            const page = await getOrderPage(searchQuery);

            setOrderPage(page);

        } finally {

            setLoading(false);

        }

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

        void loadOrderPage(nextQuery);

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

        void loadOrderPage(nextQuery);

    }

    /**
     * View order detail
     */
    async function handleView(order) {

        const detail = await getOrderDetail(
            order.id
        );

        setOrderDetail(detail);

        setDetailOpen(true);

    }

    /**
     * Close detail dialog
     */
    function handleCloseDetail() {

        setDetailOpen(false);

        setOrderDetail(null);

    }

    useEffect(() => {

        void loadOrderPage(query);

    }, []);

    return (

        <>

            <PageHeader
                title="Order Management"
                description="Manage customer orders"
            />

            <PageContainer>

                <OrderSearchForm
                    onSearch={handleSearch}
                />

                <OrderTable
                    loading={loading}
                    orderPage={orderPage}
                    query={query}
                    onPageChange={handlePageChange}
                    onView={handleView}
                />

            </PageContainer>

            <OrderDetailModal
                open={detailOpen}
                order={orderDetail}
                onCancel={handleCloseDetail}
            />

        </>

    );

}

export default OrderPage;