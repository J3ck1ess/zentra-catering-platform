import PageContainer from "../../components/page/PageContainer";
import PageHeader from "../../components/pageHeader/PageHeader";
import EmployeeSearchForm from "./components/EmployeeSearchForm";
import EmployeeTable from "./components/EmployeeTable";

/**
 * Employee management page
 */
function EmployeePage() {
    return (
        <>

            <PageHeader
                title="Employee Management"
                description="Manage employee accounts and permissions"
            />

            <PageContainer>

                <EmployeeSearchForm />

                <hr className="my-6" />

                <EmployeeTable />

                <hr className="my-6" />

                Pagination

            </PageContainer>

        </>
    );
}

export default EmployeePage;