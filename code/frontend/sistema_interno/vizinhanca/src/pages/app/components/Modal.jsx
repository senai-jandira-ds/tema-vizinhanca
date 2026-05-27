import React, { useState } from 'react';
import styles from './Modal.module.css';
import { getCondominiumData } from '../../../services/authService';

export default function Modal({
  isOpen,
  onClose,
  data,
  type = 'usuario',
  onSubmit,
  onDelete
}) {
  if (!isOpen) return null;

  const renderContent = () => {
    switch (type) {
      case 'usuario':
        return (
          <UsuarioModal
            data={data}
            onClose={onClose}
            onSubmit={onSubmit}
            onDelete={onDelete}
          />
        );

      case 'categoria':
        return (
          <CategoriaModal
            data={data}
            onClose={onClose}
            onSubmit={onSubmit}
            onDelete={onDelete}
          />
        );

      case 'servico':
        return (
          <ServicoModal
            data={data}
            onClose={onClose}
            onSubmit={onSubmit}
            onDelete={onDelete}
          />
        );

      case 'denuncia':
        return (
          <DenunciaModal
            data={data}
            onClose={onClose}
            onSubmit={onSubmit}
            onDelete={onDelete}
          />
        );

      default:
        return (
          <UsuarioModal
            data={data}
            onClose={onClose}
            onSubmit={onSubmit}
            onDelete={onDelete}
          />
        );
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div
        className={styles.modalContainer}
        onClick={(e) => e.stopPropagation()}
      >
        {renderContent()}
      </div>
    </div>
  );
}

function UsuarioModal({
  data,
  onClose,
  onSubmit,
  onDelete
}) {
  const isEdicao =
    data?.linha !== null &&
    data?.linha !== undefined;

  const condominioData = getCondominiumData();

  const [formData, setFormData] = useState({
    nome: data?.linha?.nome || "",
    apto: data?.linha?.apto || "",
    bloco: data?.linha?.bloco || "",
    cpf:
      (data?.linha?.cpf &&
        data?.linha.cpf.replace(/\D/g, '')) || "",
    email: data?.linha?.email || "",
    telefone: data?.linha?.telefone || "",
    status: data?.linha?.status || "",
    condominium_id:
      condominioData?.id ||
      data?.linha?.condominium_id ||
      null,
    id: data?.linha?.id || null
  });

  const handleChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSubmit = () => {
    const { id, ...dadosSemId } = formData;

    const dadosParaEnviar = {
      name: dadosSemId.nome || "",
      apartment: dadosSemId.apto || "",
      block: dadosSemId.bloco || "",
      cpf: dadosSemId.cpf || "",
      email: dadosSemId.email || "",
      phone: dadosSemId.telefone || "",
      status: dadosSemId.status || "Ativo",
      id_condominium: formData.condominium_id
    };

    if (isEdicao) {
      onSubmit && onSubmit(id, dadosParaEnviar);
    } else {
      onSubmit && onSubmit(null, dadosParaEnviar);
    }

    onClose();
  };

  const handleDelete = () => {
    if (!isEdicao || !formData.id) return;

    onDelete && onDelete(formData.id);

    onClose();
  };

  return (
    <div className={styles.userModal}>
      <div className={styles.logoContainer}>
        {isEdicao && data?.linha?.photo ? (
          <img
            src={data.linha.photo}
            alt={formData.nome || "Foto do usuário"}
            className={styles.userPhoto}
          />
        ) : (
          <svg
            viewBox="0 0 537 529"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              fillRule="evenodd"
              clipRule="evenodd"
              d="M202.585 1.07198..."
              fill="#2EA9F5"
            />
          </svg>
        )}
      </div>

      <h1>
        {isEdicao
          ? 'Detalhes do morador'
          : 'Cadastro de morador'}
      </h1>

      <div className={styles.formGroup}>
        <input
          type="text"
          value={formData.nome}
          onChange={(e) =>
            handleChange('nome', e.target.value)
          }
          placeholder="Nome"
          className={styles.inputFull}
        />

        <div className={styles.row}>
          <input
            type="text"
            value={formData.apto}
            onChange={(e) =>
              handleChange('apto', e.target.value)
            }
            placeholder="Apto"
            className={styles.inputHalf}
          />

          <select
            className={styles.selectHalf}
            value={formData.bloco}
            onChange={(e) =>
              handleChange('bloco', e.target.value)
            }
          >
            <option value="">Bloco</option>
            <option value="A">A</option>
            <option value="B">B</option>
          </select>
        </div>

        <input
          type="text"
          value={formData.cpf}
          onChange={(e) =>
            handleChange('cpf', e.target.value)
          }
          placeholder="CPF"
          className={styles.inputFull}
        />

        <input
          type="email"
          value={formData.email}
          onChange={(e) =>
            handleChange('email', e.target.value)
          }
          placeholder="Email"
          className={styles.inputFull}
        />

        <div className={styles.row}>
          <input
            type="text"
            value={formData.telefone}
            onChange={(e) =>
              handleChange('telefone', e.target.value)
            }
            placeholder="Telefone"
            className={styles.inputHalf}
          />

          <select
            className={styles.selectHalf}
            value={formData.status}
            onChange={(e) =>
              handleChange('status', e.target.value)
            }
          >
            <option value="">Status</option>
            <option value="Ativo">Ativo</option>
            <option value="Inativo">Inativo</option>
          </select>
        </div>
      </div>

      <div className={styles.userButtons}>
        {isEdicao && (
          <button
            className={styles.btnBlue}
            onClick={handleDelete}
          >
            Deletar
          </button>
        )}

        <button
          className={styles.btnRed}
          onClick={onClose}
        >
          Cancelar
        </button>

        <button
          className={styles.btnGreen}
          onClick={handleSubmit}
        >
          Finalizar
        </button>
      </div>
    </div>
  );
}

function CategoriaModal({
  data,
  onClose,
  onSubmit,
  onDelete
}) {
  const isEdicao =
    data?.linha !== null &&
    data?.linha !== undefined;

  const [formData, setFormData] = useState({
    nome: data?.linha?.nome || "",
    detalhe: data?.linha?.detalhe || "",
    tipo_categoria_id:
      data?.linha?.tipo_categoria_id || "",
    status: data?.linha?.status || "Disponível",
    id: data?.linha?.id || null
  });

  const handleChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSubmit = () => {
    const dadosParaEnviar = {
      name: formData.nome,
      description: formData.detalhe,
      type_category_id: Number(
        formData.tipo_categoria_id
      ),
      status: formData.status
    };

    if (isEdicao) {
      onSubmit &&
        onSubmit(formData.id, dadosParaEnviar);
    } else {
      onSubmit &&
        onSubmit(null, dadosParaEnviar);
    }

    onClose();
  };

  const handleDelete = () => {
    if (!isEdicao || !formData.id) return;

    onDelete && onDelete(formData.id);

    onClose();
  };

  return (
    <div className={styles.userModal}>
      <h1>
        {isEdicao
          ? 'Detalhes da categoria'
          : 'Cadastro de categoria'}
      </h1>

      <div className={styles.formGroup}>
        <input
          type="text"
          value={formData.nome}
          onChange={(e) =>
            handleChange('nome', e.target.value)
          }
          placeholder="Nome"
          className={styles.inputFull}
        />

        <input
          type="text"
          value={formData.detalhe}
          onChange={(e) =>
            handleChange('detalhe', e.target.value)
          }
          placeholder="Detalhe"
          className={styles.inputFull}
        />

        <select
          className={styles.inputFull}
          value={formData.tipo_categoria_id}
          onChange={(e) =>
            handleChange(
              'tipo_categoria_id',
              e.target.value
            )
          }
        >
          <option value="">
            Tipo da categoria
          </option>

          <option value={1}>
            Objeto
          </option>

          <option value={2}>
            Serviço
          </option>
        </select>
      </div>

      <div className={styles.userButtons}>
        {isEdicao && (
          <button
            className={styles.btnBlue}
            onClick={handleDelete}
          >
            Deletar
          </button>
        )}

        <button
          className={styles.btnRed}
          onClick={onClose}
        >
          Cancelar
        </button>

        <button
          className={styles.btnGreen}
          onClick={handleSubmit}
        >
          Finalizar
        </button>
      </div>
    </div>
  );
}

function ServicoModal({ data, onClose }) {
  return (
    <SplitLayoutModal
      data={data}
      onClose={onClose}
      isServico={true}
    />
  );
}

function DenunciaModal({ data, onClose }) {
  return (
    <SplitLayoutModal
      data={data}
      onClose={onClose}
      isServico={false}
    />
  );
}

function SplitLayoutModal({
  data,
  onClose,
  isServico
}) {
  const imagemRelacionada =
    data?.linha?.imagem ||
    "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?q=80&w=800&auto=format&fit=crop";

  console.log("TESTANDO ENDPOINT");
  console.log(data);

  return (
    <div className={styles.splitModal}>
      <div className={styles.imageSide}>
        <img
          src={imagemRelacionada}
          alt="Imagem de evidência"
        />
      </div>

      <div className={styles.contentSide}>
        <div className={styles.contentScroll}>
          <h1>Detalhes</h1>

          <div className={styles.textSection}>
            <label>Descrição</label>

            <p>
              {data?.linha?.descricao ||
                "Erro, nenhuma informação foi encontrada em nosso sistema"}
            </p>
          </div>

          <div className={styles.readOnlyFields}>
            <div className={styles.fieldGroup}>
              <label>Tipo</label>

              <div className={styles.fakeInput}>
                {data?.linha?.tipo ||
                  (isServico
                    ? "Serviço"
                    : "Denúncia")}
              </div>
            </div>

            <div className={styles.fieldGroup}>
              <label>Status</label>

              <div className={styles.fakeInput}>
                {data?.linha?.status ||
                  "Pendente"}
              </div>
            </div>

            <div className={styles.fieldGroup}>
              <label>Endereço</label>

              <div className={styles.fakeInput}>
                {data?.linha?.endereco ||
                  "Área Comum"}
              </div>
            </div>
          </div>
        </div>

        <div className={styles.splitButtons}>
          <button
            className={styles.btnIconBlue}
            title="Excluir"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M3 6h18"></path>

              <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>

              <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            </svg>
          </button>

          <div className={styles.actionButtons}>
            <button
              className={styles.btnBlue}
              onClick={onClose}
            >
              Cancelar
            </button>

            <button className={styles.btnGreen}>
              Finalizar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}