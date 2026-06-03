import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus, updateActivity, deleteActivity } from "../../services/activityService";
import { toast } from 'react-toastify';
import styles from "./Services.module.css";

function Services() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [dadosFiltrados, setDadosFiltrados] = useState([]);
    const [filtrosSelecionados, setFiltrosSelecionados] = useState({});

    useEffect(() => {
        fetchServices();
    }, []);

    useEffect(() => {
        aplicarFiltros();
    }, [filtrosSelecionados, dadosTabela]);

    const aplicarFiltros = () => {
        let filtrados = [...dadosTabela];

        // Aplicar filtros selecionados
        Object.entries(filtrosSelecionados).forEach(([secao, opcoes]) => {
            if (opcoes && opcoes.length > 0) {
                if (secao === 'Status') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.status));
                } else if (secao === 'Tipo') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.categoria));
                }
            }
        });

        setDadosFiltrados(filtrados);
    };

    const fetchServices = async () => {
        try {
            setLoading(true);
            const data = await getActivities();

            const services = data.response?.activities

            if (!Array.isArray(services)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = services
                .map((activity) => ({
                    id: activity.resident_id?.toString() || '',
                    nome: activity.resident_name || '',
                    descricao: activity.description || '',
                    categoria: 'Serviço',
                    status: formatActivityStatus(activity.status),
                    data: formatActivityDate(activity.creation_date)
                }));

            setDadosTabela(mappedData);
        } catch (error) {
            toast.error('Erro ao buscar serviços');
            setDadosTabela([]);
        } finally {
            setLoading(false);
        }
    };

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome', width: 220 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'categoria', label: 'Categoria', width: 150 },
        {
            id: 'status',
            label: 'Status',
            width: 150,
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

    const handleSubmitService = async (id, dados) => {
        try {
            await updateActivity(id, dados);
            toast.success("Serviço finalizado com sucesso!");
            fetchServices();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao finalizar serviço");
        }
    };

    const handleDeleteService = async (id) => {
        try {
            await deleteActivity(id);
            toast.success("Serviço excluído com sucesso!");
            fetchServices();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao excluir serviço");
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
                <h1>Serviços</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                    <FilterOptions
                        filterConfig={{
                            Status: ["Aberto", "Pendente", "Em andamento", "Finalizado", "Cancelado"],
                            Tipo: ["Serviço", "Objeto"]
                        }}
                        onFilterChange={setFiltrosSelecionados}
                    />
                    <Searchbar placeholder="Pesquisar serviço por nome ou descrição" type="text" />
                </div>
                <Table
                    columns={colunasTabela}
                    data={dadosFiltrados}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    modalType="servico"
                    exportType="servicos"
                    onSubmit={handleSubmitService}
                    onDelete={handleDeleteService}
                />
            </main>
        </>
    );
}

export default Services;