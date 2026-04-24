import { useState } from 'react'
import logo from "./assets/img/icons/logo.png";
import condominio from "./assets/img/icons/condominio.png";
import LoginInput from "./components/login/logininput";
import './App.css'

function App() {


  return (
      <div className="screen-content">
        <div className="left-content">
          <a href="">Voltar</a>
          <div className="left-main">
          <img src={logo} alt="" />
          <LoginInput />
          <LoginInput />
          <button>Entrar</button>
          <p>
          Ainda não tem uma conta?<span> Crie uma aqui!</span>
          </p>
          </div>
        </div>
        <div className="right-content">
          <img src={condominio} alt="" />
        </div>
      </div>
  )
}

export default App
