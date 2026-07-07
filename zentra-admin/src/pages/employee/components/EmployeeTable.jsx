/**
 * Employee table
 */
function EmployeeTable() {
    return (
        <div className="overflow-hidden rounded-lg border border-gray-200">

            <table className="min-w-full">

                <thead className="bg-gray-50">

                <tr>

                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Username
                    </th>

                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Name
                    </th>

                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Role
                    </th>

                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Status
                    </th>

                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Operation
                    </th>

                </tr>

                </thead>

                <tbody>

                <tr>

                    <td className="px-4 py-3 text-sm text-gray-700">
                        admin
                    </td>

                    <td className="px-4 py-3 text-sm text-gray-700">
                        System Administrator
                    </td>

                    <td className="px-4 py-3 text-sm text-gray-700">
                        SUPER_ADMIN
                    </td>

                    <td className="px-4 py-3 text-sm text-green-600">
                        Active
                    </td>

                    <td className="px-4 py-3 text-sm text-gray-700">
                        Edit | Delete
                    </td>

                </tr>

                </tbody>

            </table>

        </div>
    );
}

export default EmployeeTable;