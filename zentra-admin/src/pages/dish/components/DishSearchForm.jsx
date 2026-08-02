import { Button, Form, Input, Select, Space } from "antd";

/**
 * Dish search form
 */
function DishSearchForm({
                            onSearch,
                            onCreate,
                            categories,
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
                    label="Dish Name"
                    name="name"
                >
                    <Input
                        placeholder="Enter dish name"
                        allowClear
                        style={{ width: 220 }}
                    />
                </Form.Item>

                <Form.Item
                    label="Category"
                    name="categoryId"
                >
                    <Select
                        placeholder="Select category"
                        allowClear
                        style={{ width: 200 }}
                        options={categories?.map(category => ({
                            value: category.id,
                            label: category.name,
                        }))}
                    />
                </Form.Item>

                <Form.Item
                    label="Status"
                    name="status"
                >
                    <Select
                        placeholder="Select status"
                        allowClear
                        style={{ width: 160 }}
                        options={[
                            {
                                value: 1,
                                label: "Enabled",
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

                        <Button
                            type="primary"
                            onClick={onCreate}
                        >
                            New Dish
                        </Button>

                    </Space>

                </Form.Item>

            </Form>

        </section>
    );
}

export default DishSearchForm;