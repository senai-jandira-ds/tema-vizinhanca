import styles from "./style.module.css";
import Sidebar from "./components/sidebar";

function Dashboard() {

    return (
        <>
        <div className={styles.background}></div>
        <div className={styles.screenContent}>
            <Sidebar />
            <div className={styles.testContent}></div>
        </div>
        </>
    )
}

export default Dashboard;