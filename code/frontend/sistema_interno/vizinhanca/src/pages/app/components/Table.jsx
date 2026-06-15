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
import Modal from './Modal';

export default function Table({
  columns = [],
  data = [],
  onCellClick = null,
  exportType = null,
  showPagination = false,
  showExport = true,
  pageSize = 8,
  maxHeight = null,
  modalType = '',
  onSubmit = null,
  onDelete = null,
  onCadastrarNovo = null,
  // Backend pagination props
  useBackendPagination = false,
  totalPages = null,
  currentPage = 0,
  onPageChange = null
}) {

  const [modalOpen, setModalOpen] = useState(false);
  const [cellData, setCellData] = useState(null);
  const [dynamicModalType, setDynamicModalType] = useState('');

  const [sorting, setSorting] = useState([]);

  // Só usar paginação interna se não estiver usando paginação do backend
  const [internalPagination, setInternalPagination] = useState({
    pageIndex: 0,
    pageSize
  });

  const pagination = useBackendPagination
    ? { pageIndex: currentPage, pageSize }
    : internalPagination;

  const setPagination = useBackendPagination
    ? () => { } // Não fazer nada, controle é do backend
    : setInternalPagination;

  const [columnWidths, setColumnWidths] = useState({});

  const containerRef = useRef(null);

  useEffect(() => {
    const widths = {};

    columns.forEach((col) => {
      widths[col.id] = col.width || 150;
    });

    setColumnWidths(widths);
  }, [columns]);

  const [containerHeight, setContainerHeight] = useState('100%');

  useEffect(() => {
    if (containerRef.current && maxHeight) {
      setContainerHeight(`${maxHeight}px`);
    }
  }, [maxHeight]);

  const tableColumns = useMemo(() => {
    const columnHelper = createColumnHelper();

    return columns.map((col) =>
      columnHelper.accessor(col.id, {
        id: col.id,
        header: col.label,

        cell: (info) => {
          const value = info.getValue();
          const row = info.row.original;

          if (col.render && typeof col.render === 'function') {
            return col.render(value, row);
          }

          return value;
        },

        enableSorting: true,
      })
    );
  }, [columns]);

  const table = useReactTable({
    data,
    columns: tableColumns,

    manualPagination: useBackendPagination,

    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),

    getPaginationRowModel:
      showPagination && !useBackendPagination
        ? getPaginationRowModel()
        : undefined,

    state: {
      sorting,
      pagination: showPagination
        ? pagination
        : undefined,
    },

    onSortingChange: setSorting,
    onPaginationChange: setPagination,

    initialState: {
      pagination: {
        pageSize,
      },
    },
  });

  const iniciarRedimensionamento = (e, colunaId) => {

    e.preventDefault();
    e.stopPropagation();

    const startX = e.clientX;
    const larguraInicial = columnWidths[colunaId];

    const aoArrastar = (eventoArrastar) => {

      const novaLargura = Math.max(
        50,
        larguraInicial + eventoArrastar.clientX - startX
      );

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

  const handleCliqueCelula = (valor, colunaId, linha) => {

    // Determinar o modalType (se modalType for função, chamar com a linha)
    let resolvedModalType = modalType;
    if (typeof modalType === 'function') {
      resolvedModalType = modalType(linha);
    }

    if (onCellClick) {
      onCellClick(valor, colunaId, linha);
    }

    setCellData({
      valor,
      colunaId,
      linha
    });

    setDynamicModalType(resolvedModalType);
    setModalOpen(true);
  };

  const obterClasseCelula = (coluna, valor) => {

    if (
      coluna.getCellClass &&
      typeof coluna.getCellClass === 'function'
    ) {
      return coluna.getCellClass(valor);
    }

    return '';
  };

  const headerGroups = table.getHeaderGroups();
  const rowModel = table.getRowModel();

  // Usar paginação do backend ou front
  const itemsPerPage = useBackendPagination
    ? totalPages || 1
    : table.getPageCount();

  const displayCurrentPage = useBackendPagination
    ? currentPage
    : pagination.pageIndex;

  const irParaPagina = (pagina) => {
    if (useBackendPagination && onPageChange) {
      onPageChange(pagina);
    } else {
      setPagination((prev) => ({
        ...prev,
        pageIndex: pagina
      }));
    }
  };

  return (
    <div className={styles.tabelaWrapper}>

      <div
        ref={containerRef}
        className={styles['tabela-container']}
        style={{
          height: maxHeight
            ? containerHeight
            : undefined
        }}
      >

        <table>

          <thead>

            {headerGroups.map((headerGroup) => (

              <tr key={headerGroup.id}>

                {headerGroup.headers.map((header) => {

                  const col = columns.find(
                    (c) => c.id === header.id
                  );

                  const width =
                    columnWidths[header.id] || 150;

                  const sortDirection =
                    sorting.find((s) => s.id === header.id)?.desc
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

                        {flexRender(
                          header.column.columnDef.header,
                          header.getContext()
                        )}

                        <SortIcon direction={sortDirection} />

                        {col && (
                          <div
                            className={styles.redimensionador}
                            onMouseDown={(e) =>
                              iniciarRedimensionamento(e, col.id)
                            }
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

                  const col = columns.find(
                    (c) => c.id === cell.column.id
                  );

                  const cellClass =
                    col
                      ? obterClasseCelula(col, cell.getValue())
                      : '';

                  return (

                    <td
                      key={cell.id}
                      className={cellClass}
                      onClick={(e) => {

                        e.stopPropagation();

                        handleCliqueCelula(
                          cell.getValue(),
                          cell.column.id,
                          row.original
                        );
                      }}
                    >

                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}

                    </td>
                  );
                })}

              </tr>
            ))}

          </tbody>

        </table>

      </div>

      <div className={styles.actionsBar}>

        {showExport && (
          <ExportButton
            data={data}
            columns={columns}
            type={exportType}
          />
        )}

        {(modalType === 'usuario' ||
          modalType === 'categoria' ||
          modalType === 'bloco') &&
          onCadastrarNovo && (

            <button
              className={styles.btnCadastrar}
              onClick={() => {
                setCellData(null);
                setDynamicModalType(modalType);
                setModalOpen(true);
              }}
            >

              {modalType === 'categoria'
                ? 'Cadastrar Categoria'
                : modalType === 'bloco'
                  ? 'Cadastrar Bloco'
                  : 'Cadastrar Morador'}

            </button>
          )}

        {showPagination && itemsPerPage > 1 && (

          <div className={styles.paginacaoBotoes}>

            <button
              className={`${styles['btn-paginacao']} ${styles.arrowButton} ${displayCurrentPage === 0 ? styles.inativo : ''}`}
              onClick={() => irParaPagina(0)}
              disabled={displayCurrentPage === 0}
            >
              {'<<'}
            </button>

            <button
              className={`${styles['btn-paginacao']} ${styles.arrowButton} ${displayCurrentPage === 0 ? styles.inativo : ''}`}
              onClick={() => irParaPagina(displayCurrentPage - 1)}
              disabled={displayCurrentPage === 0}
            >
              {'<'}
            </button>

            {[...Array(itemsPerPage)].map((_, i) => {

              if (
                i === 0 ||
                i === itemsPerPage - 1 ||
                (i >= displayCurrentPage - 1 &&
                  i <= displayCurrentPage + 1)
              ) {

                return (
                  <button
                    key={i}
                    className={`${styles['btn-paginacao']} ${i === displayCurrentPage ? styles.ativo : ''}`}
                    onClick={() => irParaPagina(i)}
                  >
                    {i + 1}
                  </button>
                );
              }

              return null;
            })}

            <button
              className={`${styles['btn-paginacao']} ${styles.arrowButton} ${displayCurrentPage === itemsPerPage - 1 ? styles.inativo : ''}`}
              onClick={() => irParaPagina(displayCurrentPage + 1)}
              disabled={displayCurrentPage === itemsPerPage - 1}
            >
              {'>'}
            </button>

            <button
              className={`${styles['btn-paginacao']} ${styles.arrowButton} ${displayCurrentPage === itemsPerPage - 1 ? styles.inativo : ''}`}
              onClick={() => irParaPagina(itemsPerPage - 1)}
              disabled={displayCurrentPage === itemsPerPage - 1}
            >
              {'>>'}
            </button>

          </div>
        )}

      </div>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        data={cellData}
        type={dynamicModalType}
        onSubmit={onSubmit}
        onDelete={onDelete}
      />

    </div>
  );
}