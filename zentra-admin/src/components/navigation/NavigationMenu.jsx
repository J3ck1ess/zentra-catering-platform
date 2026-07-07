import { NavLink } from "react-router-dom";
import menuItems from "../../config/menu";

/**
 * Navigation menu
 */
function NavigationMenu() {
    return (
        <nav className="px-3 py-4">
            {
                menuItems.map((item) => (
                    <NavLink
                        key={item.key}
                        to={item.path}
                        className={({ isActive }) =>
                            [
                                "mt-1 flex rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                                isActive
                                    ? "bg-gray-900 text-white"
                                    : "text-gray-700 hover:bg-gray-100",
                            ].join(" ")
                        }
                    >
                        {item.title}
                    </NavLink>
                ))
            }
        </nav>
    );
}

export default NavigationMenu;