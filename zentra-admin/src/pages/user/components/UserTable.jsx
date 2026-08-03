import { Switch, Table } from "antd";

/**
 * User table
 */
function UserTable({
                       loading,
                       userPage,
                       query,
                       onPageChange,
                       onStatusChange,
                   }) {

    const columns = [

        {
            title: "ID",
            dataIndex: "id",
            key: "id",
        },

        {
            title: "Username",
            dataIndex: "username",
            key: "username",
        },

        {
            title: "Status",
            dataIndex: "status",
            key: "status",
            render: (status, record) => (

                <Switch
                    checked={status === 1}
                    checkedChildren="Active"
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
            title: "Created Time",
            dataIndex: "createdAt",
            key: "createdAt",
            render: (createdAt) =>
                createdAt?.replace("T", " "),
        },

    ];

    return (

        <Table
            rowKey="id"
            loading={loading}
            columns={columns}
            dataSource={userPage?.records ?? []}
            pagination={{
                current: query.page,
                pageSize: query.pageSize,
                total: userPage?.total ?? 0,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: onPageChange,
            }}
        />

    );

}

export default UserTable;