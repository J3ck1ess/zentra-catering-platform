import { Button, Popconfirm, Space, Table } from "antd";

/**
 * Category table
 */
function CategoryTable({
    loading,
    categoryPage,
    query,
    onPageChange,
    onEdit,
    onDelete,
}) {

    const columns = [

        {
            title: "Category Name",
            dataIndex: "name",
            key: "name",
        },

        {
            title: "Sort",
            dataIndex: "sort",
            key: "sort",
        },

        {
            title: "Description",
            dataIndex: "description",
            key: "description",
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
                        title="Delete category"
                        description="Are you sure you want to delete this category?"
                        okText="Delete"
                        cancelText="Cancel"
                        onConfirm={() => onDelete(record.id)}
                    >

                        <Button
                            danger
                            type="link"
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
            dataSource={categoryPage?.records ?? []}
            pagination={{
                current: query.page,
                pageSize: query.pageSize,
                total: categoryPage?.total ?? 0,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: onPageChange,
            }}
        />

    );

}

export default CategoryTable;