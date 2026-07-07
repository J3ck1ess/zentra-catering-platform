/**
 * Application navigation menu
 */
const menuItems = [
    {
        key: "dashboard",
        title: "Dashboard",
        path: "/",
        icon: null,
        permission: null,
        hidden: false,
    },
    {
        key: "employee",
        title: "Employee",
        path: "/employees",
        icon: null,
        permission: "employee:view",
        hidden: false,
    },
    {
        key: "category",
        title: "Category",
        path: "/categories",
        icon: null,
        permission: "category:view",
        hidden: false,
    },
    {
        key: "dish",
        title: "Dish",
        path: "/dishes",
        icon: null,
        permission: "dish:view",
        hidden: false,
    },
    {
        key: "order",
        title: "Order",
        path: "/orders",
        icon: null,
        permission: "order:view",
        hidden: false,
    },
    {
        key: "user",
        title: "User",
        path: "/users",
        icon: null,
        permission: "user:view",
        hidden: false,
    },
];

export default menuItems;