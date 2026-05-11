import Searchbar from "../../components/ui/SearchBar";
import Table from "./components/Table";
import FilterOptions from "../../components/ui/Filter"
import styles from "./Activity.module.css";

function Activity() {

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ', width: 200 },
        { id: 'descricao', label: 'Descrição', width: 350 },
        { id: 'categoria', label: 'Categoria ', width: 150 },
        {
            id: 'status',
            label: 'Status ',
            width: 150,
            getCellClass: (status) => {
                if (status === 'Aberto' || status === 'Disponível') return styles['status-verde'];
                if (status === 'Concluído') return styles['status-azul'];
                return '';
            }
        },
    ];

    const dadosTabela = [
        { id: '5524', nome: 'João Silva', descricao: 'Preciso de uma furadeira por 1...', categoria: 'Pedido', status: 'Aberto' },
        { id: '3392', nome: 'Maria Oliveira', descricao: 'Escada disponível para emprést..', categoria: 'Objeto', status: 'Disponível' },
        { id: '5522', nome: 'João Pereira', descricao: 'Preciso de uma furadeira por 1...', categoria: 'Pedido', status: 'Aberto' },
        { id: '20', nome: 'Maria Ribeiro', descricao: 'Escada disponível para emprést..', categoria: 'Objeto', status: 'Disponível' },
        
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log('Clicou na célula:', { valor, colunaId, linha });
    };

    return (
        <>
            <header className={styles.header}>
                <h1>Pedidos e Objetos</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                <FilterOptions/>
                <Searchbar placeholder="Pesquisar Nº ou Nome" type="text"  />
                </div>
                <Table
                    columns={colunasTabela}
                    data={dadosTabela}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    exportType="atividade-geral"
                />
            </main>
        </>
    );
}

export default Activity;