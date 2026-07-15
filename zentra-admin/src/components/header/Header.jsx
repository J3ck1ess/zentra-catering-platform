import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCurrentEmployee } from "../../pages/employee/api/employeeApi";
import { removeToken } from "../../services/auth/tokenService";

/**
 * Application header
 */
function Header() {

    const [currentEmployee, setCurrentEmployee] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {

        async function loadCurrentEmployee() {

            try {

                const employee = await getCurrentEmployee();

                setCurrentEmployee(employee);

            } catch (error) {

                console.error(error);

            }

        }

        void loadCurrentEmployee();

    }, []);

    /**
     * Handle logout
     */
    function handleLogout() {

        removeToken();

        navigate("/login", {
            replace: true,
        });

    }

    return (
        <header className="flex h-16 items-center justify-between border-b border-gray-200 bg-white px-6">

            <div>
                <h1 className="text-xl font-semibold text-gray-900">
                    Dashboard
                </h1>
            </div>

            <div className="flex items-center gap-4">

                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-900 text-sm font-semibold text-white">
                    {currentEmployee?.name?.charAt(0) ?? "?"}
                </div>

                <div>

                    <p className="text-sm font-semibold text-gray-900">
                        {currentEmployee?.role}
                    </p>

                    <p className="text-xs text-gray-500">
                        {currentEmployee?.name}
                    </p>

                    <button
                        onClick={handleLogout}
                        className="mt-1 text-xs text-red-500 hover:text-red-700"
                    >
                        Logout
                    </button>

                </div>

            </div>

        </header>
    );
}

export default Header;