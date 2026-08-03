import { Descriptions, Divider, Modal, Table, Tag } from "antd";

/**
 * Order detail modal
 */
function OrderDetailModal({
                              open,
                              order,
                              onCancel,
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
            title: "Dish",
            dataIndex: "dishName",
            key: "dishName",
        },

        {
            title: "Price",
            dataIndex: "price",
            key: "price",
            render: (price) =>
                Number(price).toFixed(2),
        },

        {
            title: "Quantity",
            dataIndex: "quantity",
            key: "quantity",
        },

        {
            title: "Subtotal",
            dataIndex: "amount",
            key: "amount",
            render: (amount) =>
                Number(amount).toFixed(2),
        },

    ];

    return (

        <Modal
            title="Order Detail"
            open={open}
            footer={null}
            width={900}
            onCancel={onCancel}
            destroyOnHidden
        >

            {

                order && (

                    <>

                        <Descriptions
                            bordered
                            column={2}
                        >

                            <Descriptions.Item label="Order Number">
                                {order.orderNumber}
                            </Descriptions.Item>

                            <Descriptions.Item label="Status">
                                {renderStatus(order.status)}
                            </Descriptions.Item>

                            <Descriptions.Item label="Created Time">
                                {order.createdAt}
                            </Descriptions.Item>

                            <Descriptions.Item label="Total Amount">
                                {Number(order.totalAmount).toFixed(2)}
                            </Descriptions.Item>

                        </Descriptions>

                        <Divider />

                        <Table
                            rowKey="dishId"
                            columns={columns}
                            dataSource={order.items}
                            pagination={false}
                        />

                    </>

                )

            }

        </Modal>

    );

}

export default OrderDetailModal;