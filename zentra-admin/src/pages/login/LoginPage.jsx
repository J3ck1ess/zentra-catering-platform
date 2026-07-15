import { Button, Card, Form, Input, message } from "antd";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login } from "./api/loginApi";
import { setToken } from "../../services/auth/tokenService";

/**
 * Login page
 */
function LoginPage() {

    const navigate = useNavigate();

    const [messageApi, contextHolder] = message.useMessage();

    const [loading, setLoading] = useState(false);

    /**
     * Handle login
     */
    async function handleLogin(values) {

        try {

            setLoading(true);

            const loginResponse = await login(values);

            const token = loginResponse.token;

            setToken(token);

            messageApi.success("Login successful.");

            navigate("/", {
                replace: true,
            });

        } catch (error) {

            const errorMessage =
                error.message ?? "Login failed";

            messageApi.error(errorMessage);

        } finally {

            setLoading(false);

        }

    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">

            <Card
                title="Zentra Admin"
                className="w-[420px]"
            >

                {contextHolder}

                <Form
                    layout="vertical"
                    onFinish={handleLogin}
                >

                    <Form.Item
                        label="Username"
                        name="username"
                        rules={[
                            {
                                required: true,
                                message: "Please enter your username",
                            },
                        ]}
                    >
                        <Input
                            placeholder="Enter username"
                            allowClear
                        />
                    </Form.Item>

                    <Form.Item
                        label="Password"
                        name="password"
                        rules={[
                            {
                                required: true,
                                message: "Please enter your password",
                            },
                        ]}
                    >
                        <Input.Password
                            placeholder="Enter password"
                        />
                    </Form.Item>

                    <Form.Item className="mb-0">
                        <Button
                            block
                            type="primary"
                            htmlType="submit"
                            loading={loading}
                        >
                            Sign In
                        </Button>
                    </Form.Item>

                </Form>

            </Card>

        </div>
    );
}

export default LoginPage;