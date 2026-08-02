import {Button, Popconfirm, Space, Switch, Table} from "antd";

/**
 * Dish table
 */
function DishTable({
                       loading,
                       dishPage,
                       query,
                       onPageChange,
                       onEdit,
                       onDelete,
                       onStatusChange,
                   }) {

    const columns = [

        {
            title: "Dish Name",
            dataIndex: "name",
            key: "name",
        },

        {
            title: "Category",
            dataIndex: "categoryName",
            key: "categoryName",
        },

        {
            title: "Price",
            dataIndex: "price",
            key: "price",
            render: (price) =>
                price == null
                    ? "-"
                    : Number(price).toFixed(2),
        },

        {
            title: "Status",
            dataIndex: "status",
            key: "status",
            render: (status, record) => (

                <Switch
                    checked={status === 1}
                    checkedChildren="Enabled"
                    unCheckedChildren="Disabled"
                    onChange={(checked) =>
                        onStatusChange(
                            record.id,
                            checked ? 1 : 0
                        )
                    }
                />

            ),
        },

        {
            title: "Operation",
            key: "operation",
            render: (_, record) => (

                <Space>

                    <Button
                        type="link"
                        onClick={() => onEdit(record)}
                    >
                        Edit
                    </Button>

                    <Popconfirm
                        title="Delete dish"
                        description="Are you sure you want to delete this dish?"
                        okText="Delete"
                        cancelText="Cancel"
                        onConfirm={() => onDelete(record.id)}
                    >

                        <Button
                            type="link"
                            danger
                        >
                            Delete
                        </Button>

                    </Popconfirm>

                </Space>

            ),
        },

    ];

    return (

        <Table
            rowKey="id"
            columns={columns}
            loading={loading}
            dataSource={dishPage?.records ?? []}
            pagination={{
                current: query.page,
                pageSize: query.pageSize,
                total: dishPage?.total ?? 0,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: onPageChange,
            }}
        />

    );

}

export default DishTable;