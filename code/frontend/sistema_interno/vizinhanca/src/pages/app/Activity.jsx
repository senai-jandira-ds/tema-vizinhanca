import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus, formatActivityType, updateActivity, deleteActivity } from "../../services/activityService";
import { toast } from 'react-toastify';
import styles from "./Activity.module.css";

function Activity() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);
    const [filtrosSelecionados, setFiltrosSelecionados] = useState({});


    useEffect(() => {
        fetchActivities();
    }, []);

    useEffect(() => {
        aplicarFiltrosEBusca();
    }, [termoBusca, dadosTabela, filtrosSelecionados]);

    const aplicarFiltrosEBusca = () => {
        let filtrados = [...dadosTabela];

        // Aplicar filtros selecionados
        Object.entries(filtrosSelecionados).forEach(([secao, opcoes]) => {
            if (opcoes && opcoes.length > 0) {
                if (secao === 'Status') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.status));
                } else if (secao === 'Categoria') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.categoria));
                }
            }
        });

        // Aplicar busca por texto
        if (termoBusca.trim()) {
            const termoLower = termoBusca.toLowerCase();
            filtrados = filtrados.filter(dado =>
                dado.nome.toLowerCase().includes(termoLower) ||
                dado.descricao.toLowerCase().includes(termoLower)
            );
        }

        setDadosFiltrados(filtrados);
    };

    const fetchActivities = async () => {
        try {
            setLoading(true);
            const data = await getActivities();

            if (!Array.isArray(data.response.activities)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = data.response.activities.map((activity, index) => ({
                id: activity.resident_id?.toString() || (index + 1).toString(),
                nome: activity.resident_name || '',
                descricao: activity.description || '',
                categoria: formatActivityType(activity.type),
                status: formatActivityStatus(activity.status),
                data: formatActivityDate(activity.creation_date)
            }));

            setDadosTabela(mappedData);
        } catch (error) {
            toast.error('Erro ao buscar atividades');
            setDadosTabela([]);
        } finally {
            setLoading(false);
        }
    };

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome', width: 220 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'categoria', label: 'Categoria', width: 180 },
        {
            id: 'status',
            label: 'Status',
            width: 160,
            getCellClass: (status) => {
                if (status === 'Aberto' || status === 'Disponível') return styles['status-verde'];
                if (status === 'Concluido' || status === 'Finalizado') return styles['status-azul'];
                if (status === 'Pendente' || status === 'Indisponível' || status === 'Em andamento')return styles['status-amarelo'];
                return '';
            }
        },
        { id: 'data', label: 'Data', width: 150 }
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        // Clique na célula para abrir modal
    };

    const handleSubmitActivity = async (id, dados) => {
        try {
            await updateActivity(id, dados);
            toast.success("Atividade finalizada com sucesso!");
            fetchActivities();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao finalizar atividade");
        }
    };

    const handleDeleteActivity = async (id) => {
        try {
            await deleteActivity(id);
            toast.success("Atividade excluída com sucesso!");
            fetchActivities();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao excluir atividade");
        }
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
                <h1>Atividade Geral</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                    <FilterOptions
                        filterConfig={{
                            Status: ["Aberto", "Pendente", "Em andamento", "Finalizado", "Cancelado"],
                            Categoria: ["Serviço", "Publicação", "Denúncia"]
                        }}
                        onFilterChange={setFiltrosSelecionados}
                    />
                    <Searchbar
                    placeholder="Pesquisar atividade por nome ou status"
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
                    modalType="servico"
                    exportType="atividade-geral"
                    onSubmit={handleSubmitActivity}
                    onDelete={handleDeleteActivity}
                />
            </main>
        </>
    );
}

export default Activity;