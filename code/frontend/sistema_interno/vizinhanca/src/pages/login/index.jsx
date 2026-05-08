import styles from "./style.module.css";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import logo from "../../assets/icons/logo.png";
import condominio from "../../assets/icons/condominio.png";
import Input from "../../components/ui/Input";
import { loginCondominium, saveToken, saveCondominiumData } from "../../services/authService";



function LoginScreen() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await loginCondominium(email, password);

            if (response.response?.token) {
                saveToken(response.response.token);
                // Salva os dados do condomínio (id, nome, etc)
                if (response.response.user) {
                    saveCondominiumData(response.response.user);
                }
                navigate('/app');
            } else {
                setError('Email ou senha incorretos');
            }
        } catch (err) {
            if (err.code === 'ERR_NETWORK') {
                setError('Erro de conexão. Verifique se o servidor está rodando.');
            } else {
                setError('Erro ao fazer login. Verifique suas credenciais.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.screenContent}>
            <div className={styles.leftContent}>
                <a className={styles.back} href="/landing_page/index.html">Voltar</a>

                <div className={styles.leftMain}>
                    <img src={logo} alt="" />

                    <Input
                        placeholder="Login"
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <Input
                        placeholder="Senha"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    {error && <p className={styles.error}>{error}</p>}

                    <button onClick={handleLogin} disabled={loading}>
                        {loading ? 'Entrando...' : 'Entrar'}
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