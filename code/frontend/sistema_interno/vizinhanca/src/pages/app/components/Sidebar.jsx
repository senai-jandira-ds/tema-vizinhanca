import React from "react";
import styles from "./Sidebar.module.css";
import Back from "../../../assets/icons/backicon.svg?react";
import Home from "../../../assets/icons/homeicon.svg?react";
import People from "../../../assets/icons/peopleicon.svg?react";
import Global from "../../../assets/icons/globalicon.svg?react";
import Hands from "../../../assets/icons/handsicon.svg?react";
import Settings from "../../../assets/icons/settingsicon.svg?react";
import Shield from "../../../assets/icons/shieldsecurityicon.svg?react";
import Help from "../../../assets/icons/helpicon.svg?react";

function Sidebar() {
    return (
        <aside>
            <nav>
                <ul className={styles.list}>
                    <li>
                        <a href="">
                            <Back className={styles.icon} />
                            <span className={styles.label}>Voltar</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <Home className={styles.icon} />
                            <span className={styles.label}>Dashboard</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <Global className={styles.icon} />
                            <span className={styles.label}>Global</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <People className={styles.icon} />
                            <span className={styles.label}>Usuários</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <Hands className={styles.icon} />
                            <span className={styles.label}>Serviços</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <Shield className={styles.icon} />
                            <span className={styles.label}>Segurança</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <Settings className={styles.icon} />
                            <span className={styles.label}>Configurações</span>
                        </a>
                    </li>

                    <li className={styles.lastItem}>
                        <a href="">
                            <Help className={styles.icon} />
                            <span className={styles.label}>Ajuda</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </aside>
    );
}

export default Sidebar;