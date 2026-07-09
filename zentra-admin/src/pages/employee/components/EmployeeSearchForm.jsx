import {Button, Form, Input, Select, Space} from "antd";

/**
 * Employee search form
 */
function EmployeeSearchForm({ onSearch }) {

    const [form] = Form.useForm();

    /**
     * Handle search
     */
    function handleSearch() {

        const values = form.getFieldsValue();

        onSearch?.(values);

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
                        style={{width: 220}}
                    />
                </Form.Item>

                <Form.Item
                    label="Status"
                    name="status"
                >
                    <Select
                        placeholder="All"
                        allowClear
                        style={{width: 160}}
                        options={[
                            {
                                label: "Active",
                                value: 1,
                            },
                            {
                                label: "Disabled",
                                value: 0,
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
                            onClick={() => {

                                form.resetFields();

                                onSearch?.({});
                            }}
                        >
                            Reset
                        </Button>

                    </Space>

                </Form.Item>

            </Form>

        </section>
    );
}

export default EmployeeSearchForm;