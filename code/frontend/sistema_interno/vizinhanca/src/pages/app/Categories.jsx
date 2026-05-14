import Table from "./components/Table";
import { getCondominiumData } from "../../services/authService";
import styles from "./Information.module.css";
import settingsStyles from "./Settings.module.css";

function Categories() {

    const data = getCondominiumData();

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome', width: 200 },
        { id: 'detalhe', label: 'Detalhe', width: 350 },
        { id: 'tipo', label: 'Tipo', width: 150 },
        {
            id: 'status',
            label: 'Status',
            width: 150,
            getCellClass: (status) => {
                if (status === 'Aberto' || status === 'Disponível') {
                    return settingsStyles['status-verde'];
                }

                if (status === 'Concluído') {
                    return settingsStyles['status-azul'];
                }

                return '';
            }
        },
    ];

    const dadosTabela = [
        {
            id: '5524',
            nome: 'João Pereira',
            detalhe: 'Preciso de uma furadeira...',
            tipo: 'Pedido',
            status: 'Aberto'
        },
        {
            id: '3392',
            nome: 'Maria Oliveira',
            detalhe: 'Escada disponível...',
            tipo: 'Objeto',
            status: 'Disponível'
        },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log(valor, colunaId, linha);
    };

    return (
        <>
            <h2 className={styles.title}>
                Categorias cadastradas para os moradores
            </h2>

            <Table
                columns={colunasTabela}
                data={dadosTabela}
                onCellClick={handleCellClick}
                showPagination={true}
            />
        </>
    );
}

export default Categories;