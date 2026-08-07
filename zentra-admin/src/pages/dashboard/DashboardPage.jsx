import {useEffect, useState} from "react";

import {
    Button,
    Card,
    Col,
    Row,
    Space,
    Spin,
    Statistic,
} from "antd";

import {useNavigate} from "react-router-dom";

import PageHeader from "../../components/pageHeader/PageHeader";
import PageContainer from "../../components/page/PageContainer";

import {getDashboardStatistics} from "./api/dashboardApi";

/**
 * Dashboard page
 */
function DashboardPage() {

    const [loading, setLoading] = useState(false);

    const [statistics, setStatistics] = useState(null);

    const navigate = useNavigate();

    /**
     * Load dashboard statistics
     */
    async function loadDashboard() {

        try {

            setLoading(true);

            const data =
                await getDashboardStatistics();

            setStatistics(data);

        } finally {

            setLoading(false);

        }

    }

    useEffect(() => {

        void loadDashboard();

    }, []);

    return (

        <>

            <PageHeader
                title="Dashboard"
                description="Welcome to Zentra Catering Platform"
            />

            <PageContainer>

                <Spin spinning={loading}>

                    {/* Welcome */}

                    <Card
                        className="mb-6"
                    >

                        <h2 className="text-xl font-semibold mb-2">
                            Welcome back!
                        </h2>

                        <p>
                            Zentra Catering Platform
                        </p>

                        <p className="text-gray-500 mt-2">
                            Enterprise Catering SaaS Management System
                        </p>

                    </Card>

                    {/* Business Statistics */}
                    <h3 className="text-lg font-semibold mt-6 mb-4">
                        Business Statistics
                    </h3>

                    <Row gutter={[16, 16]}>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Employees"
                                    value={statistics?.employeeCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Users"
                                    value={statistics?.userCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Categories"
                                    value={statistics?.categoryCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Dishes"
                                    value={statistics?.dishCount ?? 0}
                                />
                            </Card>
                        </Col>

                    </Row>

                    {/* Order Overview */}
                    <h3 className="text-lg font-semibold mt-6 mb-4">
                        Order Overview
                    </h3>

                    <Row
                        gutter={[16, 16]}
                    >

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Pending"
                                    value={statistics?.pendingOrderCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Paid"
                                    value={statistics?.paidOrderCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Completed"
                                    value={statistics?.completedOrderCount ?? 0}
                                />
                            </Card>
                        </Col>

                        <Col span={6}>
                            <Card>
                                <Statistic
                                    title="Cancelled"
                                    value={statistics?.cancelledOrderCount ?? 0}
                                />
                            </Card>
                        </Col>

                    </Row>

                    <Card
                        className="mt-4"
                        title="Quick Actions"
                    >

                        <Space wrap>

                            <Button
                                type="primary"
                                onClick={() => navigate("/employees")}
                            >
                                Employees
                            </Button>

                            <Button
                                type="primary"
                                onClick={() => navigate("/categories")}
                            >
                                Categories
                            </Button>

                            <Button
                                type="primary"
                                onClick={() => navigate("/dishes")}
                            >
                                Dishes
                            </Button>

                            <Button
                                type="primary"
                                onClick={() => navigate("/orders")}
                            >
                                Orders
                            </Button>

                            <Button
                                type="primary"
                                onClick={() => navigate("/users")}
                            >
                                Users
                            </Button>

                        </Space>

                    </Card>

                </Spin>

            </PageContainer>

        </>

    );

}

export default DashboardPage;