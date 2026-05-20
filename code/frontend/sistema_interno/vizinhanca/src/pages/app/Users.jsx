import Searchbar from "../../components/ui/SearchBar";
import Table from "./components/Table";

import FilterOptions from "../../components/ui/Filter"
import styles from "./Users.module.css";
import { useState, useEffect } from "react";
import { getResidents, updateResident, deleteResident, createResident } from "../../services/residentService";
import { formatarCPF } from "../../utils/format";

function Users() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);

    useEffect(() => {
        fetchResidents();
    }, []);

    useEffect(() => {
        if (!termoBusca.trim()) {
            setDadosFiltrados(dadosTabela);
            return;
        }

        const termoLower = termoBusca.toLowerCase();
        const filtrados = dadosTabela.filter(dado =>
            dado.nome.toLowerCase().includes(termoLower) ||
            dado.email.toLowerCase().includes(termoLower) ||
            dado.apto.toLowerCase().includes(termoLower) ||
            dado.cpf.toLowerCase().includes(termoLower) ||
            dado.id.toLowerCase().includes(termoLower) ||
            dado.status.toLowerCase().includes(termoLower)
        );
        setDadosFiltrados(filtrados);
    }, [termoBusca, dadosTabela]);

        const fetchResidents = async () => {
        try {
            setLoading(true);
            const response = await getResidents();
            console.log('Resposta da API (moradores):', response);
            const residents = response?.response || [];

            if (!Array.isArray(residents)) {
                setDadosTabela([]);
                return;
            }


            const mappedData = residents.map(resident => ({
                id: resident.id?.toString() || '',
                nome: resident.name || '',
                email: resident.email || '',
                telefone: resident.phone || '',
                cpf: formatarCPF(resident.cpf) || '',
                status: 'Ativo',
                apto: resident.apartment
            }));

            console.log('Dados mapeados:', mappedData);
            setDadosTabela(mappedData);
            setDadosFiltrados(mappedData);
        } catch (error) {
            setDadosTabela([]);
            setDadosFiltrados([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitUpdate = async (id, dados) => {
        console.log(dados)
        try {
            if (id) {
                await updateResident(id, dados);
            } else {
                // POST - Criar
                console.log('POST /resident - Dados:', dados);
                await createResident(dados);
            }
            fetchResidents();
        } catch (error) {
        }
    };

    const handleDeleteUpdate = async (id) => {
        try {
            console.log(`DELETE /resident/${id}`);
            await deleteResident(id);
            fetchResidents();
        } catch (error) {
        }
    };

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ', width: 250 },
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
        alert('Clicou na célula: ' + valor);
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
                <FilterOptions/>
                <Searchbar
                    placeholder="Pesquisar por nome ou email"
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
                />
            </main>
        </>
    );
}

export default Users;
