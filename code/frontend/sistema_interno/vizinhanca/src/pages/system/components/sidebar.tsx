import React from "react";
import "./sidebar.module.css";

function Sidebar() {
    
    return (
        <aside>
            <nav>
                <nav>
                <ul>
                <li><a href=""><img src={"../icons/homeicon"} alt="" /> Voltar</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Home</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Atividade Geral</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Condôminos</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Pedidos e Objetos</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Denúncias</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Configurações do Condomínio</a></li>
                <li><a href=""><img src={"../icons/homeicon"} alt="" />Ajuda</a></li>
                </ul>
                </nav>
            </nav>
        </aside>
    )
}

export default Sidebar;