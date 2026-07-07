/**
 * Page header
 */
function PageHeader({
                        title,
                        description,
                        actions,
                    }) {
    return (
        <div className="mb-6 flex items-start justify-between">

            <div>

                <h1 className="text-3xl font-bold text-gray-900">
                    {title}
                </h1>

                {
                    description && (
                        <p className="mt-2 text-gray-500">
                            {description}
                        </p>
                    )
                }

            </div>

            {
                actions && (
                    <div>
                        {actions}
                    </div>
                )
            }

        </div>
    );
}

export default PageHeader;