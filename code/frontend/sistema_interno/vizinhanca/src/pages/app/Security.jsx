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

            const reports = data?.response?.content;

            if (!Array.isArray(reports)) {
                setDadosTabela([]);
                return;
            }
            
            console.log(data)

        const mappedData = reports.map((report) => ({
            id: report.id?.toString() || '',
            autor: report.resident?.name || 'Não identificado',
            photo: report.resident.photo || report.object.photo || report.report.photo,
            descricao: report.description || '',
            motivo: report.reason_report.name || '',
            status: report.status || '',
            data: report.creation_date || ''
        }));

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
        { id: 'autor', label: 'Autor', width: 220 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'motivo', label: 'Motivo', width: 350 },
        {
            id: 'status',
            label: 'Status',
            width: 180,
            getCellClass: (status) => {
                if (status === 'Spam' || status === 'Falso') return styles['status-amarelo'];
                if (status === 'Ofensivo' || status === 'Violência') return styles['status-vermelho'];
                if (status === 'Inapropriado') return styles['status-azul'];
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
                    modalType="denuncia"
                    exportType="denuncias"
                />
            </main>
        </>
    );
}

export default Security;