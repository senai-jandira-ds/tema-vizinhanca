import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getReports, formatReportType, formatReportReason } from "../../services/reportService";
import styles from "./Security.module.css";

function Security() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);

    useEffect(() => {
        fetchReports();
    }, []);

    const fetchReports = async () => {
        try {
            setLoading(true);
            const data = await getReports();

            if (!Array.isArray(data)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = data.response.reports.map((report) => ({
                id: report.id?.toString() || '',
                nome: report.resident?.name || 'Não identificado',
                descricao: report.description || '',
                categoria: formatReportType(report),
                status: formatReportReason(report.reasonReport?.name) || ''
            }));

            console.log('Dados mapeados:', mappedData);
            setDadosTabela(mappedData);
        } catch (error) {
            console.error('Erro ao buscar denúncias:', error);
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
            label: 'Motivo',
            width: 180,
            getCellClass: (status) => {
                if (status === 'Spam' || status === 'Falso') return styles['status-amarelo'];
                if (status === 'Ofensivo' || status === 'Violência') return styles['status-vermelho'];
                if (status === 'Inapropriado') return styles['status-azul'];
                return '';
            }
        }
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
                <h1>Denúncias</h1>
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
                    exportType="denuncias"
                />
            </main>
        </>
    );
}

export default Security;