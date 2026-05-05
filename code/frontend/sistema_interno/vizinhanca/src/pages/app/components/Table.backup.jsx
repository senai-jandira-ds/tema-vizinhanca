import React, { useState } from 'react';
import styles from './Table.module.css';

const dadosIniciais = [
  { id: '5524', nome: 'João Pereira', detalhe: 'Preciso de uma furadeira por 1...', tipo: 'Pedido', status: 'Aberto' },
  { id: '3392', nome: 'Maria Oliveira', detalhe: 'Escada disponível para emprést..', tipo: 'Objeto', status: 'Disponível' },
  { id: '3393', nome: 'Pedro Santos', detalhe: 'Posso ajudar com a mudança hoje', tipo: 'Interação', status: 'Concluído' },
  { id: '3393', nome: 'Pedro Santos', detalhe: 'Posso ajudar com a mudança hoje', tipo: 'Interação', status: 'Concluído' },
  { id: '3393', nome: 'Pedro Santos', detalhe: 'Posso ajudar com a mudança hoje', tipo: 'Interação', status: 'Concluído' },
];

export default function Tabela() {
  const [larguras, setLarguras] = useState({
    id: 100,
    nome: 200,
    detalhe: 350,
    tipo: 150,
    status: 150,
  });

  const handleCliqueCelula = (dado, coluna) => {
    alert(`Você clicou na célula: ${dado}`);
  };

  const iniciarRedimensionamento = (e, colunaId) => {
    e.preventDefault();
    const startX = e.clientX;
    const larguraInicial = larguras[colunaId];

    const aoArrastar = (eventoArrastar) => {
      const novaLargura = Math.max(50, larguraInicial + eventoArrastar.clientX - startX);
      setLarguras((prev) => ({
        ...prev,
        [colunaId]: novaLargura,
      }));
    };

    const pararArrastar = () => {
      document.removeEventListener('mousemove', aoArrastar);
      document.removeEventListener('mouseup', pararArrastar);
    };

    document.addEventListener('mousemove', aoArrastar);
    document.addEventListener('mouseup', pararArrastar);
  };

  const obterClasseStatus = (status) => {
    if (status === 'Aberto' || status === 'Disponível') return styles['status-verde'];
    if (status === 'Concluído') return styles['status-azul'];
    return '';
  };

  return (
    <div className={styles.tabelaWrapper}>
      <div className={styles['tabela-container']}>
        <table>
          <thead>
            <tr>
              {[
                { id: 'id', label: 'Nº' },
                { id: 'nome', label: 'Nome ↕' },
                { id: 'detalhe', label: 'Detalhe' },
                { id: 'tipo', label: 'Tipo ↕' },
                { id: 'status', label: 'Status ↕' },
              ].map((col) => (
                <th key={col.id} style={{ width: larguras[col.id] }}>
                  {col.label}
                  <div
                    className={styles.redimensionador}
                    onMouseDown={(e) => iniciarRedimensionamento(e, col.id)}
                  />
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {dadosIniciais.map((linha, index) => (
              <tr key={index}>
                <td onClick={() => handleCliqueCelula(linha.id, 'id')}>{linha.id}</td>
                <td onClick={() => handleCliqueCelula(linha.nome, 'nome')}>{linha.nome}</td>
                <td onClick={() => handleCliqueCelula(linha.detalhe, 'detalhe')}>{linha.detalhe}</td>
                <td onClick={() => handleCliqueCelula(linha.tipo, 'tipo')}>{linha.tipo}</td>
                <td 
                  onClick={() => handleCliqueCelula(linha.status, 'status')}
                  className={obterClasseStatus(linha.status)}
                >
                  {linha.status}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className={styles.paginacao}>
        <button className={`${styles['btn-paginacao']} ${styles.ativo}`}>{'<'}</button>
        <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>1</button>
        <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>2</button>
        <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>3</button>
        <button className={`${styles['btn-paginacao']} ${styles.ativo}`}>{'>'}</button>
      </div>
    </div>
  );
}