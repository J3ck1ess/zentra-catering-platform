import { useEffect, useState } from "react";
import { ROLE_OPTIONS } from "../../../constants/roleOptions";

import {
    Button,
    Form,
    Input,
    Modal,
    Select,
} from "antd";

function EmployeeCreateModal({
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
            title="Create Employee"
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
                    label="Username"
                    name="username"
                    rules={[
                        {
                            required: true,
                            message: "Please enter username.",
                        },
                        {
                            max: 32,
                            message: "Username cannot exceed 32 characters.",
                        },
                    ]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    label="Password"
                    name="password"
                    rules={[
                        {
                            required: true,
                            message: "Please enter password.",
                        },
                        {
                            min: 6,
                            message: "Password must be at least 6 characters.",
                        },
                        {
                            max: 32,
                            message: "Password cannot exceed 32 characters.",
                        },
                    ]}
                >

                    <Input.Password />

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
                        Create
                    </Button>

                </Form.Item>

            </Form>

        </Modal>

    );

}

export default EmployeeCreateModal;