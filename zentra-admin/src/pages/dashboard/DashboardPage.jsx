import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";

/**
 * Dashboard page
 */
function DashboardPage() {
    return (
        <>

            <PageHeader
                title="Dashboard"
                description="Welcome to Zentra Enterprise Admin Platform"
            />

            <PageContainer>

                Dashboard Content

            </PageContainer>

        </>
    );
}

export default DashboardPage;