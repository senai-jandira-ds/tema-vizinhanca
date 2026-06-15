import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getReports, deleteReport, updateReport, formatReportType, formatReportReason, formatStatus, formatReportDate } from "../../services/reportService";
import { toast } from 'react-toastify';
import styles from "./Security.module.css";

function Security() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [dadosFiltrados, setDadosFiltrados] = useState([]);
    const [filtrosSelecionados, setFiltrosSelecionados] = useState({});
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10,
        totalPages: 1,
        totalElements: 0
    });

    useEffect(() => {
        fetchReports();
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
                }
            }
        });

        setDadosFiltrados(filtrados);
    };

    const fetchReports = async (page = pagination.page, size = pagination.size) => {
        try {
            setLoading(true);
            const data = await getReports(page, size);

            const reports = data?.response?.content || [];
            const pageInfo = data?.response;

            if (!Array.isArray(reports)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = reports.map((report) => ({
                id: report.id?.toString() || '',
                autor: report.resident?.name || 'Não identificado',
                photo: report.resident?.photo || '',
                descricao: report.description || '',
                motivo: report.reason_report?.name || '',
                status: formatStatus(report.status) || '',
                data: formatReportDate(report.creation_date || ''),
                entityId: report.id
            }));

            setDadosTabela(mappedData);
            setDadosFiltrados(mappedData);

            // Atualizar informações de paginação
            setPagination({
                page: pageInfo?.current_page || 0,
                size: pageInfo?.page_size || size,
                totalPages: pageInfo?.total_pages || 1,
                totalElements: pageInfo?.total_elements || 0
            });
        } catch (error) {
            toast.error('Erro ao buscar denúncias');
            setDadosTabela([]);
            setDadosFiltrados([]);
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
        // Clique na célula para abrir modal
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < pagination.totalPages) {
            fetchReports(newPage, pagination.size);
        }
    };

    const handleDeleteReport = async (linha) => {
        try {
            await deleteReport(linha.entityId);
            toast.success("Denúncia excluída com sucesso!");
            fetchReports(pagination.page, pagination.size);
        } catch (error) {
            console.error('Erro ao excluir denúncia:', error);
            toast.error(error.response?.data?.message || "Erro ao excluir denúncia");
        }
    };

const handleUpdateReport = async (linha, dadosEditados) => {
    // 1. Preparamos o objeto com os campos obrigatórios que a API exige
    const payload = {
        reasonReportId: dadosEditados.reasonReportId || linha.reasonReportId, 
        description: dadosEditados.description || linha.descricao,
        status: dadosEditados.status // ou o valor bruto que o backend espera
    };

    // 2. Validação simples antes de enviar
    if (!payload.reasonReportId || !payload.description) {
        toast.error("O Motivo e a Descrição são obrigatórios para atualizar a denúncia.");
        return;
    }

    try {
        await updateReport(linha.entityId, payload);
        toast.success("Denúncia atualizada com sucesso!");
        fetchReports(pagination.page, pagination.size);
    } catch (error) {
        // Agora você verá no console exatamente qual campo o backend está reclamando
        console.error('Detalhes do erro:', error.response?.data);
        toast.error("Erro ao atualizar: " + (error.response?.data?.message || "Verifique os campos obrigatórios"));
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
                <h1>Denúncias</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                    <FilterOptions
                        filterConfig={{
                            Status: ["Aberto", "Pendente", "Em andamento", "Finalizado", "Cancelado"]
                        }}
                        onFilterChange={setFiltrosSelecionados}
                    />
                    <Searchbar placeholder="Pesquisar denúncia por autor ou descrição" type="text" />
                </div>
                <Table
                    columns={colunasTabela}
                    data={dadosFiltrados}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    modalType="denuncia"
                    exportType="denuncias"
                    pageSize={8}
                    onDelete={handleDeleteReport}
                    onSubmit={handleUpdateReport}
                />
            </main>
        </>
    );
}

export default Security;
