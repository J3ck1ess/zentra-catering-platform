import { Button, Popconfirm, Space, Switch, Table } from "antd";

/**
 * Employee table
 */
function EmployeeTable({
    loading,
    employeePage,
    query,
    onPageChange,
    onStatusChange,
    onEdit,
    onDelete,
}) {

    const columns = [

        {
            title: "Username",
            dataIndex: "username",
            key: "username",
        },

        {
            title: "Name",
            dataIndex: "name",
            key: "name",
        },

        {
            title: "Role",
            dataIndex: "role",
            key: "role",
        },

        {
            title: "Status",
            dataIndex: "status",
            key: "status",
            render: (_, record) => (
                <Switch
                    checked={record.status === 1}
                    checkedChildren="Active"
                    unCheckedChildren="Disabled"
                    onChange={(checked) =>
                        onStatusChange(record.id, checked)
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
                        title="Delete employee"
                        description="Are you sure you want to delete this employee?"
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
            dataSource={employeePage?.records ?? []}
            pagination={{
                current: query.page,
                pageSize: query.pageSize,
                total: employeePage?.total ?? 0,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: onPageChange,
            }}
        />

    );
}

export default EmployeeTable;