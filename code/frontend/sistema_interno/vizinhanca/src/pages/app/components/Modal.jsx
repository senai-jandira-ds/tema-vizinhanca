import React, { useState } from 'react';
import styles from './Modal.module.css';
import { getCondominiumData } from '../../../services/authService';

export default function Modal({ isOpen, onClose, data, type = 'usuario', onSubmit, onDelete }) {
  if (!isOpen) return null;

  const renderContent = () => {
    switch (type) {
      case 'usuario':
        return <UsuarioModal data={data} onClose={onClose} onSubmit={onSubmit} onDelete={onDelete} />;
      case 'servico':
        return <ServicoModal data={data} onClose={onClose} onSubmit={onSubmit} onDelete={onDelete} />;
      case 'denuncia':
        return <DenunciaModal data={data} onClose={onClose} onSubmit={onSubmit} onDelete={onDelete} />;
      default:
        return <UsuarioModal data={data} onClose={onClose} onSubmit={onSubmit} onDelete={onDelete} />;
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalContent}>
          {renderContent()}
        </div>
      </div>
    </div>
  );
}

function UsuarioModal({ data, onClose, onSubmit, onDelete }) {
  const isEdicao = data?.linha !== null;
  const condominioData = getCondominiumData();

  const [formData, setFormData] = useState({
    foto: data?.linha?.foto || "",
    nome: data?.linha?.nome || "",
    apto: data?.linha?.apto || "",
    cpf: (data?.linha?.cpf && data?.linha.cpf.replace(/\D/g, '')) || "",
    email: data?.linha?.email || "",
    telefone: data?.linha?.telefone || "",
    pontuacao: data?.linha?.pontuacao || 0,
    condominium_id: condominioData?.id || data?.linha?.condominium_id || null,
    id: data?.linha?.id || null
  });

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = () => {
    const { id, ...dadosSemId } = formData;

    const dadosParaEnviar = {
      photo: dadosSemId.foto || "",
      name: dadosSemId.nome || "",
      apartment: dadosSemId.apto || "",
      cpf: dadosSemId.cpf || "",
      email: dadosSemId.email || "",
      phone: dadosSemId.telefone || "",
      score: dadosSemId.pontuacao || 0,
      id_block: 1,
      condominium_id: formData.condominium_id
    };

    if (isEdicao) {
      // PUT - Atualizar
      onSubmit && onSubmit(id, dadosParaEnviar);
    } else {
      // POST - Criar
      onSubmit && onSubmit(null, dadosParaEnviar);
    }
    onClose();
  };

  const handleDelete = () => {
    if (confirm('Tem certeza que deseja excluir este morador?')) {
      onDelete && onDelete(formData.id);
      onClose();
    }
  };

  return (
    <>
      {!isEdicao && (
        <img
          className={styles.avatar}
          src="https://i.pravatar.cc/300"
          alt=""
        />
      )}

      <h1>{isEdicao ? 'Detalhes do Morador' : 'Cadastrar Morador'}</h1>

      <input
        type="text"
        value={formData.nome}
        onChange={(e) => handleChange('nome', e.target.value)}
        placeholder="Nome"
        className={styles.inputGrande}
      />

      <input
        type="text"
        value={formData.apto}
        onChange={(e) => handleChange('apto', e.target.value)}
        placeholder="Apartamento"
        className={styles.inputGrande}
      />

      <input
        type="text"
        value={formData.cpf}
        onChange={(e) => handleChange('cpf', e.target.value)}
        placeholder="CPF"
        className={styles.inputGrande}
      />

      <input
        type="email"
        value={formData.email}
        onChange={(e) => handleChange('email', e.target.value)}
        placeholder="Email"
        className={styles.inputGrande}
      />

      <div className={styles.row}>
        <input
          type="text"
          value={formData.telefone}
          onChange={(e) => handleChange('telefone', e.target.value)}
          placeholder="Telefone"
          className={styles.inputMedio}
        />

        <select
          className={styles.selectPequeno}
          value={formData.status}
          onChange={(e) => handleChange('status', e.target.value)}
        >
          <option value="">Status</option>
          <option value="Ativo">Ativo</option>
          <option value="Inativo">Inativo</option>
        </select>
      </div>

      <div className={styles.buttons}>
        <button className={styles.cancelar} onClick={onClose}>Cancelar</button>
        {isEdicao && (
          <button className={styles.deletar} onClick={handleDelete} title="Excluir morador">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 6h18"></path>
              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
          </button>
        )}
        <button className={styles.finalizar} onClick={handleSubmit}>{isEdicao ? 'Finalizar' : 'Cadastrar'}</button>
      </div>
    </>
  );
}

function ServicoModal({ data }) {
  return (
    <>
      <h1>Detalhes do Serviço</h1>

      <input
        type="text"
        defaultValue={data?.linha?.tipo || ""}
        placeholder="Tipo de Serviço"
        className={styles.inputGrande}
      />

      <input
        type="text"
        defaultValue={data?.linha?.descricao || ""}
        placeholder="Descrição"
        className={styles.inputGrande}
      />

      <div className={styles.row}>
        <input
          type="text"
          defaultValue={data?.linha?.solicitante || ""}
          placeholder="Solicitante"
          className={styles.inputMedio}
        />

        <select className={styles.selectPequeno} defaultValue={data?.linha?.status || ""}>
          <option value="">Status</option>
          <option value="Pendente">Pendente</option>
          <option value="Em Andamento">Em Andamento</option>
          <option value="Concluído">Concluído</option>
        </select>
      </div>

      <input
        type="date"
        defaultValue={data?.linha?.data || ""}
        className={styles.inputGrande}
      />

      <div className={styles.buttons}>
        <button className={styles.cancelar}>Cancelar</button>
        <button className={styles.finalizar}>Finalizar</button>
      </div>
    </>
  );
}

function DenunciaModal({ data }) {
  return (
    <>
      <h1>Detalhes da Denúncia</h1>

      <input
        type="text"
        defaultValue={data?.linha?.tipo || ""}
        placeholder="Tipo de Denúncia"
        className={styles.inputGrande}
      />

      <textarea
        defaultValue={data?.linha?.descricao || ""}
        placeholder="Descrição"
        className={styles.textarea}
        rows={4}
      />

      <div className={styles.row}>
        <input
          type="text"
          defaultValue={data?.linha?.denunciante || ""}
          placeholder="Denunciante"
          className={styles.inputMedio}
        />

        <select className={styles.selectPequeno} defaultValue={data?.linha?.status || ""}>
          <option value="">Status</option>
          <option value="Aberta">Aberta</option>
          <option value="Em Investigação">Em Investigação</option>
          <option value="Resolvida">Resolvida</option>
        </select>
      </div>

      <input
        type="date"
        defaultValue={data?.linha?.data || ""}
        className={styles.inputGrande}
      />

      <div className={styles.buttons}>
        <button className={styles.cancelar}>Cancelar</button>
        <button className={styles.finalizar}>Finalizar</button>
      </div>
    </>
  );
}
