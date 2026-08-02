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
 * Dish edit modal
 */
function DishEditModal({
                           open,
                           dish,
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

            await onSave({
                id: dish.id,
                ...values,
            });

        } finally {

            setSaving(false);

        }

    }

    useEffect(() => {

        if (!open) {
            return;
        }

        if (!dish) {

            form.resetFields();

            return;

        }

        form.setFieldsValue({
            name: dish.name,
            price: dish.price,
            categoryId: dish.categoryId,
            status: dish.status,
        });

    }, [open, dish, form]);

    return (

        <Modal
            title="Edit Dish"
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
                        Update
                    </Button>

                </Form.Item>

            </Form>

        </Modal>

    );

}

export default DishEditModal;