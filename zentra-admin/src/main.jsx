// Third-party libraries
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { ConfigProvider } from "antd";
import { RouterProvider } from "react-router-dom";

// Internal modules
import router from "./router";

// Styles
import "./index.css";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <ConfigProvider>
            <RouterProvider router={router} />
        </ConfigProvider>
    </StrictMode>
);