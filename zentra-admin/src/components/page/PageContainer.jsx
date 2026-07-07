/**
 * Page container
 */
function PageContainer({ children }) {
    return (
        <section className="rounded-xl bg-white p-6 shadow-sm">
            {children}
        </section>
    );
}

export default PageContainer;