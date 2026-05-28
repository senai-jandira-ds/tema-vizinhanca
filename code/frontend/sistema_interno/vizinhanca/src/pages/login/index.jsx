import styles from "./style.module.css";
import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import { motion } from "framer-motion";
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


    // if (loading) {
    //     return (
    //         <div className={styles.screenContent}>
    //             <div className={styles.loading}>
    //                 <div className={styles.spinner}></div>
    //             </div>
    //         </div>
    //     )
    // }

    return (
        <div
            className={styles.screenContent}

        >
            <div className={styles.leftContent}>
                <a className={styles.back} href="/landing_page/index.html">Voltar</a>

                <motion.div
                    className={styles.leftMain}
                    initial={{ opacity: 0, x: 60 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -60 }}
                    transition={{ duration: 0.35 }}
                >
                        
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

                    <button className={styles.loginButton} onClick={handleLogin} disabled={loading}>
                        {loading ? 'Entrando' : 'Entrar'}
                    </button>

                    <p className={styles.loginText}>
                        Ainda não tem uma conta? <Link to="cadastro">Crie uma aqui!</Link>
                    </p>
                </motion.div>
            </div>

            <div className={styles.rightContent}>
                <img src={condominio} alt="" />
            </div>
        </div>

        
    )
}

export default LoginScreen;