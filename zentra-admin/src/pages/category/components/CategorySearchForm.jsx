import { Button, Form, Input, Space } from "antd";

/**
 * Category search form
 */
function CategorySearchForm({
                                onSearch,
                                onCreate,
                            }) {

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
                    label="Category Name"
                    name="name"
                >
                    <Input
                        placeholder="Enter category name"
                        allowClear
                        style={{ width: 220 }}
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

                        <Button
                            type="primary"
                            onClick={onCreate}
                        >
                            New Category
                        </Button>

                    </Space>

                </Form.Item>

            </Form>

        </section>
    );
}

export default CategorySearchForm;