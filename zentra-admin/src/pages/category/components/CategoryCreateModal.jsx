import { useEffect, useState } from "react";

import {
    Button,
    Form,
    Input,
    Modal,
} from "antd";

function CategoryCreateModal({
    open,
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

        }

    }, [open, form]);

    return (

        <Modal
            title="Create Category"
            open={open}
            onCancel={onCancel}
            afterOpenChange={(visible) => {

                if (!visible) {

                    form.resetFields();

                }

            }}
            footer={null}
            destroyOnHidden
        >

            <Form
                form={form}
                layout="vertical"
                autoComplete="off"
            >

                <Form.Item
                    label="Category Name"
                    name="name"
                    rules={[
                        {
                            required: true,
                            message: "Please enter category name.",
                        },
                        {
                            max: 32,
                            message: "Category name cannot exceed 32 characters.",
                        },
                    ]}
                >
                    <Input
                        placeholder="Enter category name"
                    />
                </Form.Item>

                <Form.Item
                    label="Description"
                    name="description"
                    rules={[
                        {
                            max: 255,
                            message: "Description cannot exceed 255 characters.",
                        },
                    ]}
                >
                    <Input.TextArea
                        rows={4}
                        showCount
                        maxLength={255}
                        placeholder="Enter category description"
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

export default CategoryCreateModal;