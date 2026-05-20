import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus } from "../../services/activityService";
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

            if (!Array.isArray(data)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = data
                .map((activity) => ({
                    id: activity.id?.toString() || '',
                    nome: activity.morador || '',
                    descricao: activity.descricao || '',
                    categoria: 'Serviço',
                    status: formatActivityStatus(activity.status),
                    data: formatActivityDate(activity.dataCriacao)
                }));

            console.log('Dados mapeados:', mappedData);
            setDadosTabela(mappedData);
        } catch (error) {
            console.error('Erro ao buscar serviços:', error);
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
                if (status === 'Concluído' || status === 'Finalizado') return styles['status-azul'];
                if (status === 'Pendente' || status === 'Em andamento') return styles['status-amarelo'];
                if (status === 'Cancelado') return styles['status-vermelho'];
                return '';
            }
        },
        { id: 'data', label: 'Data', width: 150 }
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log('Clicou na célula:', { valor, colunaId, linha });
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
                    exportType="servicos"
                />
            </main>
        </>
    );
}

export default Services;