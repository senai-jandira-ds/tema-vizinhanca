import { Outlet, NavLink } from "react-router-dom";
import styles from "./Settings.module.css";

function Settings() {
    return (
        <>
            <header className={styles.header}>
                <h1>Configurações do Condomínio</h1>
            </header>

            <div className={styles.nav}>
                <NavLink to="information">Informações</NavLink>
                <NavLink to="categories">Categoria</NavLink>
                <NavLink to="blocks">Blocos</NavLink>
            </div>

            <main className={styles.main}>
                <Outlet />
            </main>
        </>
    );
}

export default Settings;