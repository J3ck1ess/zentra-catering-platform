/**
 * Application header
 */
function Header() {
    return (
        <header className="flex h-16 items-center justify-between border-b border-gray-200 bg-white px-6">

            <div>
                <h1 className="text-xl font-semibold text-gray-900">
                    Dashboard
                </h1>
            </div>

            <div className="flex items-center gap-4">

                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-900 text-sm font-semibold text-white">
                    SA
                </div>

                <div>
                    <p className="text-sm font-semibold text-gray-900">
                        SUPER_ADMIN
                    </p>

                    <p className="text-xs text-gray-500">
                        System Administrator
                    </p>
                </div>

            </div>

        </header>
    );
}

export default Header;