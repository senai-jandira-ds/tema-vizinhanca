import React, { useState, useMemo, useEffect, useRef } from 'react';
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  getPaginationRowModel,
  flexRender,
  createColumnHelper,
} from '@tanstack/react-table';
import ExportButton from "./ExportBtn";
import styles from './Table.module.css';
import SortIcon from './SortIcon';
import PaginationIcon from '../../../assets/icons/vectorarrow.png';
import Modal from './Modal';

export default function Table({
  columns = [],
  data = [],
  onCellClick = null,
  exportType = "",
  showPagination = false,
  showExport = true,
  pageSize = 8,
  maxHeight = null,
  modalType = 'usuario',
  onSubmit = null,
  onDelete = null,
  onCadastrarNovo = null
}) {
  const [modalOpen, setModalOpen] = useState(false);
  const [cellData, setCellData] = useState(null);
  const [sorting, setSorting] = useState([]);
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize });
  const [columnWidths, setColumnWidths] = useState({});
  const containerRef = useRef(null);

  // Inicializar larguras das colunas
  useEffect(() => {
    const widths = {};
    columns.forEach((col) => {
      widths[col.id] = col.width || 150;
    });
    setColumnWidths(widths);
  }, [columns]);

  // Calcular altura dinâmica baseada no container
  const [containerHeight, setContainerHeight] = useState('100%');

  useEffect(() => {
    if (containerRef.current && maxHeight) {
      setContainerHeight(`${maxHeight}px`);
    }
  }, [maxHeight]);

  // Criar colunas dinamicamente
  const tableColumns = useMemo(() => {
    const columnHelper = createColumnHelper();
    return columns.map((col) =>
      columnHelper.accessor(col.id, {
        id: col.id,
        header: col.label,
        cell: (info) => {
          const value = info.getValue();
          const row = info.row.original;

          // Renderização customizada
          if (col.render && typeof col.render === 'function') {
            return col.render(value, row);
          }

          return value;
        },
        enableSorting: true,
      })
    );
  }, [columns]);

  // Configurar tabela
  const table = useReactTable({
    data,
    columns: tableColumns,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getPaginationRowModel: showPagination ? getPaginationRowModel() : undefined,
    state: {
      sorting,
      pagination: showPagination ? pagination : undefined,
    },
    onSortingChange: setSorting,
    onPaginationChange: setPagination,
    initialState: {
      pagination: {
        pageSize,
      },
    },
  });

  // Redimensionamento de colunas
  const iniciarRedimensionamento = (e, colunaId) => {
    e.preventDefault();
    e.stopPropagation();
    const startX = e.clientX;
    const larguraInicial = columnWidths[colunaId];

    const aoArrastar = (eventoArrastar) => {
      const novaLargura = Math.max(50, larguraInicial + eventoArrastar.clientX - startX);
      setColumnWidths((prev) => ({
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

  // Handler de clique na célula
  const handleCliqueCelula = (valor, colunaId, linha) => {
    setCellData({
      valor,
      colunaId,
      linha
    });

    setModalOpen(true);
  };

  // Obter classe da célula
  const obterClasseCelula = (coluna, valor) => {
    if (coluna.getCellClass && typeof coluna.getCellClass === 'function') {
      return coluna.getCellClass(valor);
    }
    return '';
  };

  // Header groups
  const headerGroups = table.getHeaderGroups();
  const rowModel = table.getRowModel();

  // Paginação
  const itemsPerPage = table.getPageCount();
  const currentPage = pagination.pageIndex;

  const irParaPagina = (pagina) => {
    setPagination((prev) => ({ ...prev, pageIndex: pagina }));
  };

  return (
    <div className={styles.tabelaWrapper}>
      <div
        ref={containerRef}
        className={styles['tabela-container']}
        style={{ height: maxHeight ? containerHeight : undefined }}
      >
        <table>
          <thead>
            {headerGroups.map((headerGroup) => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  const col = columns.find((c) => c.id === header.id);
                  const width = columnWidths[header.id] || 150;
                  const sortDirection = sorting.find((s) => s.id === header.id)?.desc
                    ? 'desc'
                    : sorting.find((s) => s.id === header.id)
                      ? 'asc'
                      : null;

                  return (
                    <th
                      key={header.id}
                      style={{ width }}
                      onClick={header.column.getToggleSortingHandler()}
                    >
                      <div className={styles['th-content']}>
                        {flexRender(header.column.columnDef.header, header.getContext())}
                        <SortIcon direction={sortDirection} />
                        {col && (
                          <div
                            className={styles.redimensionador}
                            onMouseDown={(e) => iniciarRedimensionamento(e, col.id)}
                          />
                        )}
                      </div>
                    </th>
                  );
                })}
              </tr>
            ))}
          </thead>
          <tbody>
            {rowModel.rows.map((row) => (
              <tr key={row.id}>
                {row.getVisibleCells().map((cell) => {
                  const col = columns.find((c) => c.id === cell.column.id);
                  const cellClass = col ? obterClasseCelula(col, cell.getValue()) : '';

                  return (
                    <td
                      key={cell.id}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleCliqueCelula(cell.getValue(), cell.column.id, row.original);
                      }}
                      className={cellClass}
                    >
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className={styles.actionsBar}>
        {showExport && <ExportButton data={data} columns={columns} type={exportType} />}
        {modalType === 'usuario' && onCadastrarNovo && (
          <button
            className={styles.btnCadastrar}
            onClick={() => {
              setCellData(null);
              setModalOpen(true);
            }}
          >
            Cadastrar Morador
          </button>
        )}

        {showPagination && itemsPerPage > 1 && (
          <div className={styles.paginacaoBotoes}>

{/* PRIMEIRA PÁGINA */}
<button
  className={`${styles['btn-paginacao']} ${styles.arrowButton} ${currentPage === 0 ? styles.inativo : ''}`}
  onClick={() => irParaPagina(0)}
  disabled={currentPage === 0}
>
  <svg width="7" height="10" viewBox="0 0 7 10" fill="none">
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>

  <svg width="7" height="10" viewBox="0 0 7 10" fill="none">
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
</button>

{/* PÁGINA ANTERIOR */}
<button
  className={`${styles['btn-paginacao']} ${styles.arrowButton} ${currentPage === 0 ? styles.inativo : ''}`}
  onClick={() => irParaPagina(currentPage - 1)}
  disabled={currentPage === 0}
>
  <svg width="7" height="10" viewBox="0 0 7 10" fill="none">
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
</button>

{/* NÚMEROS */}
{[...Array(itemsPerPage)].map((_, i) => {

  if (
    i === 0 ||
    i === itemsPerPage - 1 ||
    (i >= currentPage - 1 && i <= currentPage + 1)
  ) {

    return (
      <button
        key={i}
        className={`${styles['btn-paginacao']} ${i === currentPage ? styles.ativo : ''}`}
        onClick={() => irParaPagina(i)}
      >
        {i + 1}
      </button>
    );
  }

  return null;
})}

{/* PRÓXIMA PÁGINA */}
<button
  className={`${styles['btn-paginacao']} ${styles.arrowButton} ${currentPage === itemsPerPage - 1 ? styles.inativo : ''}`}
  onClick={() => irParaPagina(currentPage + 1)}
  disabled={currentPage === itemsPerPage - 1}
>
  <svg
    width="7"
    height="10"
    viewBox="0 0 7 10"
    fill="none"
    style={{ transform: "rotate(180deg)" }}
  >
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
</button>

{/* ÚLTIMA PÁGINA */}
<button
  className={`${styles['btn-paginacao']} ${styles.arrowButton} ${currentPage === itemsPerPage - 1 ? styles.inativo : ''}`}
  onClick={() => irParaPagina(itemsPerPage - 1)}
  disabled={currentPage === itemsPerPage - 1}
>
  <svg
    width="7"
    height="10"
    viewBox="0 0 7 10"
    fill="none"
    style={{ transform: "rotate(180deg)" }}
  >
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>

  <svg
    width="7"
    height="10"
    viewBox="0 0 7 10"
    fill="none"
    style={{ transform: "rotate(180deg)" }}
  >
    <path
      d="M5.82593 0.75L1.1603 4.0405C0.897295 4.22869 0.749891 4.48233 0.749891 4.7466C0.749891 5.01088 0.897295 5.26451 1.1603 5.4527L5.82593 8.74321"
      stroke="white"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
</button>

</div>
        )}
      </div>
<Modal
  isOpen={modalOpen}
  onClose={() => setModalOpen(false)}
  data={cellData}
  type={modalType}
  onSubmit={onSubmit}
  onDelete={onDelete}
/>
    </div>
  );
}
