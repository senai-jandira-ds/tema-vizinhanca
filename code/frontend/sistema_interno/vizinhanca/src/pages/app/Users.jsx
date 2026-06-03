import Searchbar from "../../components/ui/SearchBar";
import Table from "./components/Table";

import FilterOptions from "../../components/ui/Filter"
import styles from "./Users.module.css";
import { useState, useEffect } from "react";
import { toast } from 'react-toastify';
import { getResidents, updateResident, deleteResident, createResident } from "../../services/residentService";
import { formatarCPF } from "../../utils/format";

function Users() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);
    const [filtrosSelecionados, setFiltrosSelecionados] = useState({});
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10,
        totalPages: 1,
        totalElements: 0
    });

    useEffect(() => {
        fetchResidents();
    }, []);

    useEffect(() => {
        aplicarFiltrosEBusca();
    }, [termoBusca, dadosTabela, filtrosSelecionados]);

    const aplicarFiltrosEBusca = () => {
        let filtrados = [...dadosTabela];

        // Aplicar filtros selecionados
        Object.entries(filtrosSelecionados).forEach(([secao, opcoes]) => {
            if (opcoes && opcoes.length > 0) {
                if (secao === 'Bloco') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.bloco));
                } else if (secao === 'Status') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.status));
                }
            }
        });

        // Aplicar busca por texto
        if (termoBusca.trim()) {
            const termoLower = termoBusca.toLowerCase();
            filtrados = filtrados.filter(dado =>
                dado.nome.toLowerCase().includes(termoLower) ||
                dado.email.toLowerCase().includes(termoLower) ||
                dado.apto.toLowerCase().includes(termoLower) ||
                dado.cpf.toLowerCase().includes(termoLower) ||
                dado.id.toLowerCase().includes(termoLower) ||
                dado.status.toLowerCase().includes(termoLower)
            );
        }

        setDadosFiltrados(filtrados);
    };

        const fetchResidents = async (page = pagination.page, size = pagination.size) => {
        try {
            setLoading(true);
            const response = await getResidents(page, size);

            const residents = response?.response?.content || [];
            const pageInfo = response?.response;

            if (!Array.isArray(residents)) {
                setDadosTabela([]);
                return;
            }

            console.log("---------------------")
            console.log(residents)

            const mappedData = residents.map(resident => ({
                id: resident.id?.toString() || '',
                nome: resident.name || '',
                email: resident.email || '',
                photo: resident.photo || '',
                telefone: resident.phone || '',
                cpf: formatarCPF(resident.cpf) || '',
                status: resident.is_active === Boolean(true) ? 'Ativo' : 'Inativo',
                score: resident.score || '',
                apto: resident.apartment,
                bloco: resident.block?.block || '',
                blocoId: resident.block_id || resident.block?.id || ''
            }));

            setDadosTabela(mappedData);
            setDadosFiltrados(mappedData);

            // Atualizar informações de paginação
            setPagination({
                page: pageInfo?.number || 0,
                size: pageInfo?.size || 10,
                totalPages: pageInfo?.totalPages || 1,
                totalElements: pageInfo?.totalElements || 0
            });
        } catch (error) {
            setDadosTabela([]);
            setDadosFiltrados([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitUpdate = async (id, dados) => {
        try {
            if (id) {
                await updateResident(id, dados);
                toast.success("Morador atualizado com sucesso!");
            } else {
                await createResident(dados);
                toast.success("Morador cadastrado com sucesso!");
            }
            fetchResidents();
        } catch (error) {
            console.log("---------------------")
            console.log(dados)
            console.log(error)
            console.log("---------------------")
            console.log(error.response.data.message)
            console.log("---------------------")
            toast.error(error.response?.data?.message || "Erro ao salvar morador");
        }
    };

    const handleDeleteUpdate = async (id) => {
        try {
            await deleteResident(id);
            toast.success("Morador excluído com sucesso!");
            fetchResidents(pagination.page, pagination.size);
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao excluir morador");
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < pagination.totalPages) {
            fetchResidents(newPage, pagination.size);
        }
    };

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ', width: 250 },
        { id: 'bloco', label: 'Bloco', width: 100 },
        { id: 'apto', label: 'Apto ', width: 100 },
        { id: 'cpf', label: 'CPF', width: 250 },
        { id: 'email', label: 'Email', width: 250 },
        {
            id: 'status',
            label: 'Status ',
            width: 150,
            getCellClass: (status) => {
                if (status === 'Ativo') return styles['status-verde'];
                if (status === 'Inativo') return styles['status-vermelho'];
                return '';
            }
        },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
    };

    const handleCadastrarNovo = () => {
        setCellData({ linha: null, valor: null, colunaId: null });
        setModalOpen(true);
    };

    if (loading) {
        return (
            <div className={styles.loading}>
                <div className={styles.spinner}></div>
            </div>
        );
    }

    return (
        <>
            <header className={styles.header}>
                <h1>Lista de Moradores</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                <FilterOptions
                    filterConfig={{
                        Status: ["Ativo", "Inativo"]
                    }}
                    dynamicBlocks={true}
                    onFilterChange={setFiltrosSelecionados}
                />
                <Searchbar
                    placeholder="Pesquisar por nome, email ou cpf"
                    type="text"
                    value={termoBusca}
                    onChange={(e) => setTermoBusca(e.target.value)}
                />
                </div>
                <Table
                    columns={colunasTabela}
                    data={dadosFiltrados}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    onSubmit={handleSubmitUpdate}
                    onDelete={handleDeleteUpdate}
                    onCadastrarNovo={handleCadastrarNovo}
                    exportType="moradores"
                    modalType="usuario"
                />
            </main>
        </>
    );
}

export default Users;
