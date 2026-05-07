import React from "react";
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
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
                        <Link to="/voltar">
                            <Back className={styles.icon} />
                            <span className={styles.label}>Voltar</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="/app">
                            <Home className={styles.icon} />
                            <span className={styles.label}>Dashboard</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="activity">
                            <Global className={styles.icon} />
                            <span className={styles.label}>Atividade Geral</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="users">
                            <People className={styles.icon} />
                            <span className={styles.label}>Usuários</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="/servicos">
                            <Hands className={styles.icon} />
                            <span className={styles.label}>Serviços</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="/seguranca">
                            <Shield className={styles.icon} />
                            <span className={styles.label}>Segurança</span>
                        </Link>
                    </li>

                    <li>
                        <Link to="/configuracoes">
                            <Settings className={styles.icon} />
                            <span className={styles.label}>Configurações</span>
                        </Link>
                    </li>

                    <li className={styles.lastItem}>
                        <Link to="/ajuda">
                            <Help className={styles.icon} />
                            <span className={styles.label}>Ajuda</span>
                        </Link>
                    </li>
                </ul>
            </nav>
        </aside>
    );
}

export default Sidebar;