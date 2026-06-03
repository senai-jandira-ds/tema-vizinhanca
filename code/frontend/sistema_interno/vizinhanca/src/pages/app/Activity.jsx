import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus, formatActivityType } from "../../services/activityService";
import styles from "./Activity.module.css";

function Activity() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);


    useEffect(() => {
        fetchActivities();
    }, []);   

    useEffect(() => {
        if (!termoBusca.trim()) {
            setDadosFiltrados(dadosTabela);
            return;
        }

        const termoLower = termoBusca.toLowerCase();
        const filtrados = dadosTabela.filter(dado =>
            dado.nome.toLowerCase().includes(termoLower) ||
            dado.status.toLowerCase().includes(termoLower)
        );
        setDadosFiltrados(filtrados);
    
    }, [termoBusca, dadosTabela]);

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

            console.log('Dados mapeados:', mappedData);
            setDadosTabela(mappedData);
        } catch (error) {
            console.error('Erro ao buscar atividades:', error);
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
                <h1>Atividade Geral</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                    <FilterOptions />
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
                    modalType="servico"
                    exportType="atividade-geral"
                />
            </main>
        </>
    );
}

export default Activity;