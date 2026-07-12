import { useEffect, useState } from "react";
import { ROLE_OPTIONS } from "../../../constants/roleOptions";

import {
    Button,
    Form,
    Input,
    Modal,
    Select,
} from "antd";

function EmployeeEditModal({
    open,
    employee,
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
                id: employee.id,
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

        if (!employee) {

            form.resetFields();

            return;

        }

        form.setFieldsValue({
            username: employee.username,
            name: employee.name,
            role: employee.role,
        });

    }, [open, employee, form]);

    return (

        <Modal
            title="Edit Employee"
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
            >

                <Form.Item
                    label="Username"
                    name="username"
                >
                    <Input disabled />
                </Form.Item>

                <Form.Item
                    label="Name"
                    name="name"
                    rules={[
                        {
                            required: true,
                            message: "Please enter employee name.",
                        },
                        {
                            max: 32,
                            message: "Name cannot exceed 32 characters.",
                        },
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="Role"
                    name="role"
                    rules={[
                        {
                            required: true,
                            message: "Please select a role.",
                        },
                    ]}
                >
                    <Select
                        options={ROLE_OPTIONS}
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

export default EmployeeEditModal;