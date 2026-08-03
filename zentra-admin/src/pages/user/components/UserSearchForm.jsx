import { Button, Form, Input, Select, Space } from "antd";

/**
 * User search form
 */
function UserSearchForm({
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
                    label="Username"
                    name="username"
                >
                    <Input
                        placeholder="Enter username"
                        allowClear
                    />
                </Form.Item>

                <Form.Item
                    label="Status"
                    name="status"
                >
                    <Select
                        placeholder="Select status"
                        allowClear
                        style={{ width: 180 }}
                        options={[
                            {
                                value: 1,
                                label: "Active",
                            },
                            {
                                value: 0,
                                label: "Disabled",
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

export default UserSearchForm;