import React from "react";
import "./sidebar.module.css";

function Sidebar() {
    
    return (
        <aside>
            <nav>
                <ul>
                <li><a href=""><img src={"../icons/homeicon"} alt="" /> Voltar</a></li>
                <li><a href="">Home</a></li>
                <li><a href="">Atividade Geral</a></li>
                <li><a href="">Condôminos</a></li>
                <li><a href="">Pedidos e Objetos</a></li>
                <li><a href="">Denúncias</a></li>
                <li><a href="">Configurações do Condomínio</a></li>
                <li><a href="">Ajuda</a></li>
                </ul>
            </nav>
        </aside>
    )
}

export default Sidebar;