import styles from "./style.module.css";

import { useState } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { toast } from 'react-toastify';

import { createCondominium } from "../../services/condominiumService";

import logo from "../../assets/icons/logo.png";
import condominio from "../../assets/icons/condominio.png";
import Input from "../../components/ui/Input";

function SignScreen() {

    const [name, setName] = useState("");
    const [cnpj, setCnpj] = useState("");
    const [amountBlocks, setAmountBlocks] = useState("");
    const [amountApartments, setAmountApartments] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [cep, setCep] = useState("");
    const [street, setStreet] = useState("");
    const [neighborhood, setNeighborhood] = useState("");
    const [number, setNumber] = useState("");
    const [landmark, setLandmark] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");

    const [step, setStep] = useState(1);
    const [animate, setAnimate] = useState(false);
    const [direction, setDirection] = useState("right");

    function handleNext() {
        setDirection("right");
        setAnimate(true);

        setTimeout(() => {
            setStep(2);
            setAnimate(false);
        }, 300);
    }

    function handleBack() {
        setDirection("left");
        setAnimate(true);

        setTimeout(() => {
            setStep(1);
            setAnimate(false);
        }, 300);
    }

    async function handleSubmit() {
        try {
            const data = {
                cnpj: cnpj.replace(/\D/g, ""),

                email,

                password,

                name,

                photo: "",

                amount_blocks: Number(amountBlocks),

                amount_apartments: Number(amountApartments),

                address: {
                    cep: cep.replace(/\D/g, ""),
                    street,
                    neighborhood,
                    number,
                    landmark,
                    city,
                    state: state.toUpperCase()
                }
            };

            const response = await createCondominium(data);

            toast.success("Condomínio cadastrado com sucesso!");

        } catch (error) {

            toast.error(
                error.response?.data?.message ||
                "Erro ao cadastrar condomínio"
            );
        }
    }

    return (
        <div className={styles.screenContent}>
            <div className={styles.leftContent}>
                <a
                    className={styles.back}
                    href="/landing_page/index.html"
                >
                    Voltar
                </a>
    
                <motion.div
                    className={styles.leftMain}
                    initial={{ opacity: 0, x: 60 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -60 }}
                    transition={{ duration: 0.35 }}
                >
                    <div
                        className={`
                            ${styles.stepContent}
                            ${
                                animate
                                    ? direction === "right"
                                        ? styles.slideOutLeft
                                        : styles.slideOutRight
                                    : direction === "right"
                                        ? styles.slideInRight
                                        : styles.slideInLeft
                            }
                        `}
                    >
                        <img src={logo} alt="" />
    
                        <h1 className={styles.SignTitle}>
                            Dados do Condomínio
                        </h1>
    
                        {step === 1 ? (
                            <>
                                <Input
                                    placeholder="Nome do Condomínio"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <Input
                                    placeholder="CNPJ"
                                    value={cnpj}
                                    onChange={(e) => setCnpj(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <div className={styles.condoInfos}>
                                    <Input
                                        value={amountBlocks}
                                        onChange={(e) => setAmountBlocks(e.target.value)}
                                        style={{
                                            flexGrow: 1,
                                            textAlign: "start"
                                        }}
                                        placeholder="Nº Blocos"
                                        type="number"
                                    />
    
                                    <Input
                                        value={amountApartments}
                                        onChange={(e) => setAmountApartments(e.target.value)}
                                        style={{
                                            flexGrow: 1,
                                            textAlign: "start"
                                        }}
                                        placeholder="Nº Apartamentos"
                                        type="number"
                                    />
                                </div>
    
                                <Input
                                    placeholder="Email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="email"
                                />
    
                                <Input
                                    placeholder="Senha"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="password"
                                />
    
                                <div className={styles.actionsOptions}>
                                    <button
                                        className={styles.signButton}
                                        onClick={handleNext}
                                    >
                                        <svg
                                            width="22"
                                            height="22"
                                            viewBox="0 0 22 22"
                                            fill="none"
                                            xmlns="http://www.w3.org/2000/svg"
                                        >
                                            <path
                                                d="M9.30176 1.42822L10.3857 0.344238C10.8447 -0.114746 11.5869 -0.114746 12.041 0.344238L21.5332 9.83154C21.9922 10.2905 21.9922 11.0327 21.5332 11.4868L12.041 20.979C11.582 21.438 10.8398 21.438 10.3857 20.979L9.30176 19.895C8.83789 19.4312 8.84766 18.6743 9.32129 18.2202L15.2051 12.6147H1.17188C0.522461 12.6147 0 12.0923 0 11.4429V9.88037C0 9.23096 0.522461 8.7085 1.17188 8.7085H15.2051L9.32129 3.10303C8.84277 2.64893 8.83301 1.89209 9.30176 1.42822Z"
                                                fill="white"
                                            />
                                        </svg>
                                    </button>
                                </div>
                            </>
                        ) : (
                            <>
                                <Input
                                    placeholder="CEP"
                                    value={cep}
                                    onChange={(e) => setCep(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <Input
                                    placeholder="Logradouro"
                                    value={street}
                                    onChange={(e) => setStreet(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <Input
                                    placeholder="Bairro"
                                    value={neighborhood}
                                    onChange={(e) => setNeighborhood(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <div className={styles.condoInfos}>
                                    <Input
                                        value={number}
                                        onChange={(e) => setNumber(e.target.value)}
                                        style={{
                                            flexGrow: 1,
                                            textAlign: "start"
                                        }}
                                        placeholder="Número"
                                        type="text"
                                    />
    
                                    <Input
                                        value={landmark}
                                        onChange={(e) => setLandmark(e.target.value)}
                                        style={{
                                            flexGrow: 10,
                                            textAlign: "start"
                                        }}
                                        placeholder="Ponto de Referência"
                                        type="text"
                                    />
                                </div>
    
                                <Input
                                    placeholder="Cidade"
                                    value={city}
                                    onChange={(e) => setCity(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <Input
                                    placeholder="Estado"
                                    value={state}
                                    onChange={(e) => setState(e.target.value)}
                                    style={{
                                        textAlign: "start"
                                    }}
                                    type="text"
                                />
    
                                <div className={styles.actionsOptions}>
                                    <button
                                        className={styles.backButton}
                                        style={{
                                            marginRight: "auto"
                                        }}
                                        onClick={handleBack}
                                    >
                                        <svg
                                            width="22"
                                            height="22"
                                            viewBox="0 0 22 22"
                                            fill="none"
                                            xmlns="http://www.w3.org/2000/svg"
                                            style={{
                                                transform: "rotate(180deg)"
                                            }}
                                        >
                                            <path
                                                d="M9.30176 1.42822L10.3857 0.344238C10.8447 -0.114746 11.5869 -0.114746 12.041 0.344238L21.5332 9.83154C21.9922 10.2905 21.9922 11.0327 21.5332 11.4868L12.041 20.979C11.582 21.438 10.8398 21.438 10.3857 20.979L9.30176 19.895C8.83789 19.4312 8.84766 18.6743 9.32129 18.2202L15.2051 12.6147H1.17188C0.522461 12.6147 0 12.0923 0 11.4429V9.88037C0 9.23096 0.522461 8.7085 1.17188 8.7085H15.2051L9.32129 3.10303C8.84277 2.64893 8.83301 1.89209 9.30176 1.42822Z"
                                                fill="white"
                                            />
                                        </svg>
                                    </button>
    
                                    <button
                                        className={styles.sendFormButton}
                                        onClick={handleSubmit}
                                    >
                                        Cadastrar
                                    </button>
                                </div>
                            </>
                        )}
    
                        <p className={styles.signText}>
                            Já tem uma conta?{" "}
                            <Link to="/">
                                Faça login aqui!
                            </Link>
                        </p>
                    </div>
                </motion.div>
            </div>
    
            <div className={styles.rightContent}>
                <img src={condominio} alt="" />
            </div>
        </div>
    );
}

export default SignScreen;