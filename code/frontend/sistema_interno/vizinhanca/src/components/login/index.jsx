import logo from "./assets/img/icons/logo.png";
import condominio from "./assets/img/icons/condominio.png";
import LoginInput from "./components/login/logininput";

function LoginScreen() {

    return (
        <div className="screen-content">
            <div className="left-content">
            <Link id='back' to="/">Voltar</Link>
                <div className="left-main">
                    <img src={logo} alt="" />
                    <LoginInput placeholder="Login" type="text" />
                    <LoginInput placeholder="Senha" type="password" />
                    <button type='submit'>Entrar</button>
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

