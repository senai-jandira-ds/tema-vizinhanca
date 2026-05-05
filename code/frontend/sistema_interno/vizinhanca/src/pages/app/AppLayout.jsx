import styles from "./AppLayout.module.css";
import Sidebar from "./components/Sidebar";
import { Outlet } from "react-router-dom";

function AppLayout() {
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

export default AppLayout;