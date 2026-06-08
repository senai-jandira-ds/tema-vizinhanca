import { useEffect, useState } from "react";
import Searchbar from "../../components/ui/SearchBar";
import FilterOptions from "../../components/ui/Filter";
import Table from "./components/Table";
import { getServices } from "../../services/serviceService";
import { getObjects } from "../../services/objectService";
import { formatActivityDate, formatActivityStatus } from "../../services/activityService";
import { updateService as updateServiceService, deleteService } from "../../services/serviceService";
import { updateObject as updateObjectService, deleteObject } from "../../services/objectService";
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
                    filtrados = filtrados.filter(dado => opcoes.includes(dado.tipo));
                }
            }
        });

        setDadosFiltrados(filtrados);
    };

    const fetchServices = async () => {
        try {
            setLoading(true);

            // Buscar serviços e objetos
            const [servicesData, objectsData] = await Promise.all([
                getServices(),
                getObjects()
            ]);

            const services = servicesData?.response?.content || [];
            const objects = objectsData?.response?.content || [];

            console.log(services)
            console.log(objects)

            // Mapear serviços
            const servicesMapped = services.map((service) => ({
                id: service.id?.toString() || '',
                id_resident: service.resident?.id?.toString() || '',
                nome: service.resident?.name || '',
                descricao: service.description || '',
                categoria: service.category?.name || '',
                status: formatActivityStatus(service.status),
                data: formatActivityDate(service.creation_date),
                photo: service.photo || '',
                // Dados para operações
                tipoEntidade: 'SERVICE',
                tipo: 'Serviço',
                entityId: service.id,
                urgency: service.urgency,
                estimatedTime: service.estimated_time,
                title: service.title
            }));

            // Mapear objetos
            const objectsMapped = objects.map((obj) => ({
                id: obj.id?.toString() || '',
                displayId: obj.resident?.id?.toString() || '',
                nome: obj.resident_name || '',
                descricao: obj.category.description || obj.description || '',
                categoria: obj.category?.name || '',
                status: formatActivityStatus(obj.status),
                data: obj.deadline,
                photo: obj.photo || '',
                // Dados para operações
                tipoEntidade: 'OBJECT',
                tipo: 'Objeto',
                entityId: obj.id,
                deadline: obj.deadline,
                title: obj.title
            }));

            // Combinar serviços e objetos
            const allData = [...servicesMapped, ...objectsMapped];
            setDadosTabela(allData);
            setDadosFiltrados(allData);

        } catch (error) {
            console.error('Erro ao buscar serviços:', error);
            toast.error('Erro ao buscar serviços');
            setDadosTabela([]);
            setDadosFiltrados([]);
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
                if (status === 'Pendente' || status === 'Indisponível' || status === 'Em andamento' || status === 'Emprestado') return styles['status-amarelo'];
                return '';
            }
        }
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        // Clique na célula para abrir modal
    };

    const handleSubmitService = async (id, dados) => {
        try {
            const linha = dados.linha;
            const formData = new FormData();
            if (linha.tipoEntidade === 'SERVICE') {
                

                formData.append("status", "CONCLUIDO");
                console.log([...formData.entries()]);

                await updateServiceService(id, formData);

                toast.success("Serviço concluído com sucesso!");
            } else if (linha.tipoEntidade === 'OBJECT') {

                formData.append("status", "EMPRESTADO");
                console.log([...formData.entries()]);
                await updateObjectService(id, formData);

                toast.success("Objeto finalizado com sucesso!");
            }

            fetchServices();
        } catch (error) {
            console.error('Erro ao finalizar:', error);
            toast.error(error.response?.data?.message || "Erro ao finalizar");
        }
    };

    const handleDeleteService = async (linha) => {
        try {
            if (linha.tipoEntidade === 'SERVICE') {
                await deleteService(linha.entityId);
                toast.success("Serviço excluído com sucesso!");
            } else if (linha.tipoEntidade === 'OBJECT') {
                await deleteObject(linha.entityId);
                toast.success("Objeto excluído com sucesso!");
            }

            fetchServices();
        } catch (error) {
            console.error('Erro ao excluir:', error);
            toast.error(error.response?.data?.message || "Erro ao excluir");
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
                <h1>Serviços e Objetos</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                    <FilterOptions
                        filterConfig={{
                            Status: ["Pendente", "Em andamento", "Concluído", "Cancelado"],
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
