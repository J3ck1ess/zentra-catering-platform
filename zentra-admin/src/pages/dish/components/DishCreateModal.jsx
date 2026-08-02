import { useEffect, useState } from "react";

import {
    Button,
    Form,
    Input,
    InputNumber,
    Modal,
    Select,
} from "antd";

/**
 * Dish create modal
 */
function DishCreateModal({
                             open,
                             categories,
                             onCancel,
                             onSave,
                         }) {

    const [form] = Form.useForm();

    const [saving, setSaving] = useState(false);

    /**
     * Handle save
     */
    async function handleSave() {

        try {

            setSaving(true);

            const values = await form.validateFields();

            await onSave(values);

        } finally {

            setSaving(false);

        }

    }

    useEffect(() => {

        if (open) {

            form.resetFields();

            form.setFieldsValue({
                status: 1,
            });

        }

    }, [open, form]);

    return (

        <Modal
            title="Create Dish"
            open={open}
            onCancel={onCancel}
            footer={null}
            destroyOnHidden
            afterOpenChange={(visible) => {

                if (!visible) {

                    form.resetFields();

                }

            }}
        >

            <Form
                form={form}
                layout="vertical"
                autoComplete="off"
            >

                <Form.Item
                    label="Dish Name"
                    name="name"
                    rules={[
                        {
                            required: true,
                            message: "Please enter dish name.",
                        },
                        {
                            max: 32,
                            message: "Dish name cannot exceed 32 characters.",
                        },
                    ]}
                >
                    <Input
                        placeholder="Enter dish name"
                    />
                </Form.Item>

                <Form.Item
                    label="Price"
                    name="price"
                    rules={[
                        {
                            required: true,
                            message: "Please enter price.",
                        },
                    ]}
                >
                    <InputNumber
                        style={{ width: "100%" }}
                        min={0.01}
                        precision={2}
                        placeholder="Enter price"
                    />
                </Form.Item>

                <Form.Item
                    label="Category"
                    name="categoryId"
                    rules={[
                        {
                            required: true,
                            message: "Please select category.",
                        },
                    ]}
                >
                    <Select
                        placeholder="Select category"
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

                    <Button
                        type="primary"
                        block
                        loading={saving}
                        onClick={handleSave}
                    >
                        Create
                    </Button>

                </Form.Item>

            </Form>

        </Modal>

    );

}

export default DishCreateModal;