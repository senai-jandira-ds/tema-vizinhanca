import { useEffect, useState } from "react";
import Table from "./components/Table";
import { getBlocks, createBlock, updateBlock, deleteBlock } from "../../services/blockService";
import { toast } from 'react-toastify';
import styles from "./Categories.module.css";

function Categories() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);

    useEffect(() => {
        fetchBlocks();
    }, []);

    const fetchBlocks = async () => {
        try {
            setLoading(true);

            const tableData = await getBlocks();

            console.log(tableData);

            if (!Array.isArray(tableData.response.blocks)) {
                setDadosTabela([]);
                return;
            }

            const data = tableData.response.blocks;

            const mappedData = data.map((block, index) => ({
                id: block.id?.toString() || (index + 1).toString(),
                nome: block.block || '',
            }));

            console.log('Dados mapeados:', mappedData);

            setDadosTabela(mappedData);
        } catch (error) {
            console.error('Erro ao buscar blocos:', error);
            setDadosTabela([]);
        } finally {
            setLoading(false);
        }
    };

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome', width: 300 },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log(valor, colunaId, linha);
    };

    const handleSubmitBloco = async (id, dados) => {
        try {
            if (id) {
                await updateBlock(id, dados);
                toast.success("Bloco atualizado com sucesso!");
            } else {
                await createBlock(dados);
                toast.success("Bloco cadastrado com sucesso!");
            }
            fetchBlocks();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao salvar bloco");
        }
    };

    const handleDeleteBloco = async (id) => {
        try {
            await deleteBlock(id);
            toast.success("Bloco excluído com sucesso!");
            fetchBlocks();
        } catch (error) {
            toast.error(error.response?.data?.message || "Erro ao excluir bloco");
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
        <div className={styles.container}>
            <h2 className={styles.title}>
                Blocos do condomínio
            </h2>

            <div className={styles.tableContainer}>
                <Table
                    columns={colunasTabela}
                    data={dadosTabela}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    pageSize={7}
                    modalType="bloco"
                    onCadastrarNovo={() => {}}
                    onSubmit={handleSubmitBloco}
                    onDelete={handleDeleteBloco}
                />
            </div>
        </div>
    );
}

export default Categories;