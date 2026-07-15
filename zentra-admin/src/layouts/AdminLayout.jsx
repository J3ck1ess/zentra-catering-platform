import { Navigate, Outlet } from "react-router-dom";
import Header from "../components/header/Header";
import Sidebar from "../components/sidebar/Sidebar";
import { getToken } from "../services/auth/tokenService";

/**
 * Admin layout
 */
function AdminLayout() {

    const token = getToken();

    if (!token) {

        return <Navigate to="/login" replace />;

    }

    return (
        <div className="min-h-screen bg-gray-50">

            <Header />

            <div className="flex">

                <Sidebar />

                <main className="flex-1 p-6">
                    <Outlet />
                </main>

            </div>

        </div>
    );
}

export default AdminLayout;