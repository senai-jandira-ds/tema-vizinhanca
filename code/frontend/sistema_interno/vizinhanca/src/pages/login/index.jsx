import "./style.css";
import { useNavigate } from "react-router-dom";
import logo from "../../assets/img/icons/logo.png";
import condominio from "../../assets/img/icons/condominio.png";
import LoginInput from "./components/logininput";

const navigate = useNavigate();

function LoginScreen() {

    return (
        <div className="screen-content">
            <div className="left-content">
                <a id="back" href="/landing_page/index.html">Voltar</a>
                <div className="left-main">
                    <img src={logo} alt="" />
                    <LoginInput placeholder="Login" type="text" />
                    <LoginInput placeholder="Senha" type="password" />
                    <button onClick={() => navigate("/app")}>
                        Entrar
                    </button>
                    <p>
                        Ainda não tem uma conta? <a href="">Crie uma aqui!</a>
                    </p>
                </div>
            </div>
            <div className="right-content">
                <img src={condominio} alt="" />
            </div>
        </div>
    )
}

export default LoginScreen;