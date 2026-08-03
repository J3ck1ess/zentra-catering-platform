import { Button, Form, Select, Space } from "antd";

/**
 * Order search form
 */
function OrderSearchForm({
                             onSearch,
                         }) {

    const [form] = Form.useForm();

    /**
     * Handle search
     */
    function handleSearch() {

        const values = form.getFieldsValue();

        onSearch?.(values);

    }

    /**
     * Handle reset
     */
    function handleReset() {

        form.resetFields();

        onSearch?.({});

    }

    return (

        <section className="mb-6">

            <Form
                form={form}
                layout="inline"
            >

                <Form.Item
                    label="Status"
                    name="status"
                >
                    <Select
                        placeholder="Select status"
                        allowClear
                        style={{ width: 200 }}
                        options={[
                            {
                                value: 1,
                                label: "Pending",
                            },
                            {
                                value: 2,
                                label: "Paid",
                            },
                            {
                                value: 3,
                                label: "Completed",
                            },
                            {
                                value: 4,
                                label: "Cancelled",
                            },
                        ]}
                    />
                </Form.Item>

                <Form.Item>

                    <Space>

                        <Button
                            type="primary"
                            onClick={handleSearch}
                        >
                            Search
                        </Button>

                        <Button
                            onClick={handleReset}
                        >
                            Reset
                        </Button>

                    </Space>

                </Form.Item>

            </Form>

        </section>

    );

}

export default OrderSearchForm;