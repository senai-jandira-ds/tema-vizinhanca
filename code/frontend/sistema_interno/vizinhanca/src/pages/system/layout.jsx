import styles from "./style.module.css";
import Sidebar from "./components/sidebar";
import { Outlet } from "react-router-dom";

function SystemLayout() {
    return (
        <>
            <div className={styles.background}></div>

            <div className={styles.screenContent}>
                <Sidebar />

                <div className={styles.rightContent}>
                    <Outlet />
                </div>
            </div>
        </>
    );
}

export default SystemLayout;