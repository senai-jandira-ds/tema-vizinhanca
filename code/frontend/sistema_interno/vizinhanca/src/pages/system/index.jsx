import styles from "./style.module.css";
import Sidebar from "./components/sidebar";
import Card from "./components/card.jsx";
import Table from "./components/table";

function Dashboard() {

    return (
        <>
            <div className={styles.background}></div>
            <div className={styles.screenContent}>
                <Sidebar />
                <div className={styles.rightContent}>
                    <header>
                        <h1>Dashboard</h1>
                        <div className={styles.cardsList}>
                            <Card title={"Teste"} quantity={1} />
                            <Card title={"Teste"} quantity={1} />
                            <Card title={"Teste"} quantity={1} />
                            <Card title={"Teste"} quantity={1} />
                        </div>
                    </header>
                    <main>
                        <h2>Atividade</h2>
                        <Table />
                    </main>
                </div>
            </div>
        </>
    )
}

export default Dashboard;