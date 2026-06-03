import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus } from "../../services/activityService";
import { toast } from 'react-toastify';
import styles from "./Services.module.css";

function Services() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);

    useEffect(() => {
        fetchServices();
    }, []);

    const fetchServices = async () => {
        try {
            setLoading(true);
            const data = await getActivities();

            const services = data.response?.activities

            if (!Array.isArray(services)) {
                setDadosTabela([]);
                return;
            }

            console.log(services)

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
                    <FilterOptions />
                    <Searchbar placeholder="Pesquisar Nº ou Nome" type="text" />
                </div>
                <Table
                    columns={colunasTabela}
                    data={dadosTabela}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    modalType="servico"
                    exportType="servicos"
                />
            </main>
        </>
    );
}

export default Services;