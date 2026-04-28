import styles from "./style.module.css";
import { useNavigate } from "react-router-dom";
import logo from "../../assets/img/icons/logo.png";
import condominio from "../../assets/img/icons/condominio.png";
import LoginInput from "./components/logininput";



function LoginScreen() {
    const navigate = useNavigate();
    return (
        <div className={styles.screenContent}>
            <div className={styles.leftContent}>
                <a className={styles.back} href="/landing_page/index.html">Voltar</a>

                <div className={styles.leftMain}>
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

            <div className={styles.rightContent}>
                <img src={condominio} alt="" />
            </div>
        </div>

    )
}

export default LoginScreen;