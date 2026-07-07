/**
 * Employee search form
 */
function EmployeeSearchForm() {
    return (
        <section className="mb-6">

            <label
                className="mb-2 block text-sm font-medium text-gray-700"
            >
                Username
            </label>

            <div className="flex items-center gap-3">

                <input
                    type="text"
                    placeholder="Enter username"
                    className="
                        w-80
                        rounded-lg
                        border
                        border-gray-300
                        px-3
                        py-2
                        text-sm
                        outline-none
                        transition-colors
                        focus:border-gray-900
                    "
                />

                <button
                    className="
                        rounded-lg
                        bg-gray-900
                        px-4
                        py-2
                        text-sm
                        font-medium
                        text-white
                    "
                >
                    Search
                </button>

                <button
                    className="
                        rounded-lg
                        border
                        border-gray-300
                        px-4
                        py-2
                        text-sm
                        font-medium
                        text-gray-700
                    "
                >
                    Reset
                </button>

            </div>

        </section>
    );
}

export default EmployeeSearchForm;