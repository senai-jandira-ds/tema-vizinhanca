import React from "react";
import "./sidebar.module.css";
import back from "../icons/backicon.svg";
import home from "../icons/homeicon.svg";
import people from "../icons/peopleicon.svg";
import global from "../icons/globalicon.svg";
import hands from "../icons/handsicon.svg";
import settings from "../icons/settingsicon.svg";
import shield from "../icons/shieldsecurityicon.svg";
import help from "../icons/helpicon.svg";

function Sidebar() {

    return (
        <aside>
            <nav>
                <ul>
                    <li>
                        <a href="">
                            <img src={back} alt="" />
                            <span className="label">Voltar</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={home} alt="" />
                            <span className="label">Dashboard</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={global} alt="" />
                            <span className="label">Global</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={people} alt="" />
                            <span className="label">Usuários</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={hands} alt="" />
                            <span className="label">Serviços</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={shield} alt="" />
                            <span className="label">Segurança</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={settings} alt="" />
                            <span className="label">Configurações</span>
                        </a>
                    </li>

                    <li>
                        <a href="">
                            <img src={help} alt="" />
                            <span className="label">Ajuda</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </aside>
    )
}

export default Sidebar;