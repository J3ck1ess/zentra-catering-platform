import { createBrowserRouter } from "react-router-dom";
import AdminLayout from "../layouts/AdminLayout";
import DashboardPage from "../pages/dashboard/DashboardPage";
import LoginPage from "../pages/login/LoginPage";
import EmployeePage from "../pages/employee/EmployeePage";
import CategoryPage from "../pages/category/CategoryPage";
import DishPage from "../pages/dish/DishPage";
import OrderPage from "../pages/order/OrderPage";
import UserPage from "../pages/user/UserPage";

/**
 * Application router
 */
const router = createBrowserRouter([
    {
        path: "/login",
        element: <LoginPage />,
    },
    {
        path: "/",
        element: <AdminLayout />,
        children: [
            {
                index: true,
                element: <DashboardPage />,
            },
            {
                path: "employees",
                element: <EmployeePage />,
            },
            {
                path: "categories",
                element: <CategoryPage />,
            },
            {
                path: "dishes",
                element: <DishPage />,
            },
            {
                path: "orders",
                element: <OrderPage />,
            },
            {
                path: "users",
                element: <UserPage />,
            },
        ],
    },
]);

export default router;