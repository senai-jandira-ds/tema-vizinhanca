import { useEffect, useState, useMemo } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getActivities, formatActivityDate, formatActivityStatus, formatActivityType } from "../../services/activityService";
import { updateService, deleteService } from "../../services/serviceService";
import { updateObject, deleteObject } from "../../services/objectService";
import { updateReport, deleteReport } from "../../services/reportService";
import { toast } from 'react-toastify';
import styles from "./Activity.module.css";

function Activity() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [filtrosSelecionados, setFiltrosSelecionados] = useState({});

    useEffect(() => {
        fetchActivities();
    }, []);

    const dadosFiltrados = useMemo(() => {
        let filtrados = [...dadosTabela];

        Object.entries(filtrosSelecionados).forEach(([secao, opcoes]) => {
            if (opcoes && opcoes.length > 0) {
                if (secao === 'Status') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.status));
                } else if (secao === 'Categoria') {
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.categoria));
                }
            }
        });

        if (termoBusca.trim()) {
            const termoLower = termoBusca.toLowerCase();
            filtrados = filtrados.filter(dado =>
                dado.nome.toLowerCase().includes(termoLower) ||
                dado.descricao.toLowerCase().includes(termoLower)
            );
        }

        return filtrados;
    }, [dadosTabela, termoBusca, filtrosSelecionados]);

    const fetchActivities = async () => {
        try {
            setLoading(true);
            const data = await getActivities();
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            console.log(data)
            if (!data?.response?.activities || !Array.isArray(data.response.activities)) {
                setDadosTabela([]);
                return;
            }

            const mappedData = data.response.activities.map((activity, index) => ({
                displayId: activity.resident_id?.toString() || (index + 1).toString(),
                
                // Mapeamento EXATO e seguro. Não usamos mais o displayId como fallback aqui.
                // Se a API não mandar um desses campos, o valor será null.
                idRealDaEntidade: activity.id || activity.entity_id || activity.service_id || activity.object_id || activity.report_id || null,

                tipoOriginal: activity.type, 
                nome: activity.resident_name || '',
                descricao: activity.description || '',
                categoria: formatActivityType(activity.type),
                status: formatActivityStatus(activity.status),
                data: formatActivityDate(activity.creation_date),
                rawStatus: activity.status
            }));

            setDadosTabela(mappedData);
        } catch (error) {
            toast.error('Erro ao buscar atividades');
            setDadosTabela([]);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateActivity = async (param1, param2) => {
        const linha = param2?.linha || param1?.linha || param1;
        
        if (!linha || !linha.tipoOriginal) {
            toast.error("Erro: Dados da linha não encontrados.");
            return;
        }

        const tipo = linha.tipoOriginal?.normalize('NFD').replace(/[\u0300-\u036f]/g, "").toLowerCase();
        const idDaEntidade = linha.idRealDaEntidade;

        if (!idDaEntidade) {
            toast.error("Erro Crítico: A API não forneceu o ID desta atividade. Solicite ao backend a inclusão do campo 'id' no retorno da listagem.");
            return;
        }

        try {
            if (tipo === 'servico') {
                await updateService(idDaEntidade, { status: 'Concluido' });
            } else if (tipo === 'objeto') {
                await updateObject(idDaEntidade, { status: 'FINALIZADO' });
            } else if (tipo === 'report') {
                await updateReport(idDaEntidade, { status: 'FINISHED' });
            } else {
                toast.warning("Tipo de atividade não suportado.");
                return;
            }

            toast.success("Atualizado com sucesso!");
            fetchActivities(); 
        } catch (error) {
            console.error("Erro no update:", error);
            toast.error("Erro ao atualizar atividade no servidor.");
        }
    };

    const handleDeleteActivity = async (dadoModal) => {
        const linha = dadoModal;

        if (!linha || !linha.tipoOriginal) {
            toast.error("Erro ao identificar a atividade para exclusão.");
            return;
        }

        const tipo = linha.tipoOriginal?.normalize('NFD').replace(/[\u0300-\u036f]/g, "").toLowerCase();
        const idDaEntidade = linha.idRealDaEntidade;

        if (!idDaEntidade) {
            toast.error("Erro Crítico: Impossível excluir. A API não forneceu o ID desta atividade. Solicite ao backend a inclusão do campo 'id' no retorno da listagem.");
            return;
        }

        try {
            if (tipo === 'servico') {
                await deleteService(idDaEntidade);
            } else if (tipo === 'objeto') {
                await deleteObject(idDaEntidade);
            } else if (tipo === 'report') {
                await deleteReport(idDaEntidade);
            }

            toast.success("Atividade excluída com sucesso!");
            fetchActivities(); 
        } catch (error) {
            console.error("Erro no delete:", error);
            toast.error("Erro ao tentar excluir a atividade.");
        }
    };

    const colunasTabela = [
        { id: 'displayId', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome', width: 220 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'categoria', label: 'Categoria', width: 180 },
        {
            id: 'status',
            label: 'Status',
            width: 160,
            getCellClass: (status) => {
                const statusLower = status?.toLowerCase();
                if (['aberto', 'disponível', 'disponivel'].includes(statusLower)) return styles['status-verde'];
                if (['concluido', 'concluído', 'finalizado'].includes(statusLower)) return styles['status-azul'];
                if (['pendente', 'indisponível', 'indisponivel', 'em andamento'].includes(statusLower)) return styles['status-amarelo'];
                return '';
            }
        },
        { id: 'data', label: 'Data', width: 150 }
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        const statusAtual = linha.status?.toLowerCase();
        const jaFinalizado = ['finalizado', 'concluido', 'concluído'].includes(statusAtual);

        if (jaFinalizado) {
            toast.info("Apenas visualização. Esta atividade já foi concluída.");
        }
    };

    const getModalType = (linha) => {
        if (!linha) return 'usuario';

        const tipo = linha.tipoOriginal?.toLowerCase();

        if (tipo === 'serviço' || tipo === 'servico' || tipo === 'objeto') return 'servico';
        if (tipo === 'report' || tipo === 'denúncia' || tipo === 'denuncia') return 'denuncia';
        if (tipo === 'publication' || tipo === 'publicação' || tipo === 'publicacao') return 'publicacao';

        return 'usuario';
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
                    onSubmit={handleUpdateActivity}
                    onDelete={handleDeleteActivity}
                    showPagination={true}
                    pageSize={8}
                    modalType={getModalType}
                    exportType="atividade-geral"
                />
            </main>
        </>
    );
}

export default Activity;