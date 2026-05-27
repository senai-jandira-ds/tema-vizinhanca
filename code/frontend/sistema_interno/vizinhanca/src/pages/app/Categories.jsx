import { useEffect, useState } from "react";
import Table from "./components/Table";
import { getCondominiumData } from "../../services/authService";
import { getCategoryTypes, getCategories } from "../../services/categoryService"
import styles from "./Categories.module.css";
import settingsStyles from "./Settings.module.css";

function Categories() {
    const [loading, setLoading] = useState(true);
    const [dadosTabela, setDadosTabela] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [dadosFiltrados, setDadosFiltrados] = useState([]);

    useEffect(() => {
        fetchCategories();
    }, []);   

    const fetchCategories = async () => {
        try {
            setLoading(true);
            const tableData = await getCategories();
            const categoriesType = await getCategoryTypes();

            console.log(tableData)
            console.log(categoriesType)

            if (!Array.isArray(tableData.response.categories)) {
                setDadosTabela([]);
                return;
            }
            console.log('teste')

            const data = tableData.response.categories

            const mappedData = data.map((category, index) => ({
                id: category.id?.toString() || (index + 1).toString(),
                nome: category.name || '',
                descricao: category.description || '',
                tipo: category.type_category.name
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
        { id: 'nome', label: 'Nome', width: 200 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'tipo', label: 'Tipo', width: 150 },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log(valor, colunaId, linha);
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
                Categorias cadastradas para os moradores
            </h2>
            <div className={styles.tableContainer}>
            <Table
                columns={colunasTabela}
                data={dadosTabela}
                onCellClick={handleCellClick}
                showPagination={true}
                pageSize={7}
                modalType="categoria"
                onCadastrarNovo={() => {}}
            />
            </div>
        </div>
    );
}

export default Categories;