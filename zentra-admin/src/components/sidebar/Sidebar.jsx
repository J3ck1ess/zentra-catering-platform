import menuItems from "../../config/menu";
import NavigationMenu from "../navigation/NavigationMenu";

import { NavLink } from "react-router-dom"

/**
 * Application sidebar
 */
function Sidebar() {
    return (
        <aside className="h-[calc(100vh-64px)] w-60 border-r border-gray-200 bg-white">

            <div className="border-b border-gray-200 px-6 py-5">

                <h1 className="text-2xl font-bold text-gray-900">
                    Zentra
                </h1>

                <p className="mt-1 text-sm text-gray-500">
                    Enterprise Admin
                </p>

            </div>

            <NavigationMenu />

        </aside>
    );
}

export default Sidebar;