import React, { useState } from 'react';
import styles from './Table.module.css';

export default function Table({
  columns = [],
  data = [],
  onCellClick = null,
  showPagination = false
}) {
  const inicializarLarguras = () => {
    const largurasIniciais = {};
    columns.forEach(col => {
      largurasIniciais[col.id] = col.width || 150;
    });
    return largurasIniciais;
  };

  const [larguras, setLarguras] = useState(inicializarLarguras);

  const handleCliqueCelula = (valor, colunaId, linha) => {
    if (onCellClick) {
      onCellClick(valor, colunaId, linha);
    }
  };

  const iniciarRedimensionamento = (e, colunaId) => {
    e.preventDefault();
    e.stopPropagation();
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

  const obterClasseCelula = (coluna, valor) => {
    if (coluna.getCellClass && typeof coluna.getCellClass === 'function') {
      return coluna.getCellClass(valor);
    }
    return '';
  };

  const renderizarCelula = (coluna, valor, linha) => {
    const cellClass = obterClasseCelula(coluna, valor);

    if (coluna.render && typeof coluna.render === 'function') {
      return coluna.render(valor, linha);
    }

    return (
      <td
        key={coluna.id}
        onClick={() => handleCliqueCelula(valor, coluna.id, linha)}
        className={cellClass}
      >
        {valor}
      </td>
    );
  };

  return (
    <div className={styles.tabelaWrapper}>
      <div className={styles['tabela-container']}>
        <table>
          <thead>
            <tr>
              {columns.map((col) => (
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
            {data.map((linha, index) => (
              <tr key={linha.id || index}>
                {columns.map((col) => renderizarCelula(col, linha[col.id], linha))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showPagination && (
        <div className={styles.paginacao}>
          <button className={`${styles['btn-paginacao']} ${styles.ativo}`}>{'<'}</button>
          <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>1</button>
          <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>2</button>
          <button className={`${styles['btn-paginacao']} ${styles.inativo}`}>3</button>
          <button className={`${styles['btn-paginacao']} ${styles.ativo}`}>{'>'}</button>
        </div>
      )}
    </div>
  );
}
