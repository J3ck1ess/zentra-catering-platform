import { Button, Table, Tag } from "antd";

/**
 * Order table
 */
function OrderTable({
                        loading,
                        orderPage,
                        query,
                        onPageChange,
                        onView,
                    }) {

    /**
     * Render order status
     */
    function renderStatus(status) {

        switch (status) {

            case 1:
                return <Tag color="orange">Pending</Tag>;

            case 2:
                return <Tag color="blue">Paid</Tag>;

            case 3:
                return <Tag color="green">Completed</Tag>;

            case 4:
                return <Tag color="red">Cancelled</Tag>;

            default:
                return <Tag>Unknown</Tag>;

        }

    }

    const columns = [

        {
            title: "Order Number",
            dataIndex: "orderNumber",
            key: "orderNumber",
        },

        {
            title: "Amount",
            dataIndex: "totalAmount",
            key: "totalAmount",
            render: (amount) =>
                amount == null
                    ? "-"
                    : Number(amount).toFixed(2),
        },

        {
            title: "Status",
            dataIndex: "status",
            key: "status",
            render: renderStatus,
        },

        {
            title: "Created Time",
            dataIndex: "createdAt",
            key: "createdAt",
        },

        {
            title: "Operation",
            key: "operation",
            render: (_, record) => (

                <Button
                    type="link"
                    onClick={() => onView(record)}
                >
                    View
                </Button>

            ),
        },

    ];

    return (

        <Table
            rowKey="id"
            loading={loading}
            columns={columns}
            dataSource={orderPage?.records ?? []}
            pagination={{
                current: query.page,
                pageSize: query.pageSize,
                total: orderPage?.total ?? 0,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: onPageChange,
            }}
        />

    );

}

export default OrderTable;