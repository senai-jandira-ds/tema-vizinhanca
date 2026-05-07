import Searchbar from "../../components/ui/SearchBar";
import Table from "./components/Table";
import FilterOptions from "../../components/ui/Filter"
import styles from "./Activity.module.css";

function Activity() {

    const cards = [
        { id: 1, title: "Usuários", quantity: 10, color: "#10B765" },
        { id: 2, title: "Pedidos", quantity: 5, color: "#A99817" },
        { id: 3, title: "Objetos", quantity: 2, color: "#2EA9F5" },
        { id: 4, title: "Denúncias", quantity: 1, color: "#FF1111" },
    ];

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ↕', width: 200 },
        { id: 'detalhe', label: 'Detalhe', width: 350 },
        { id: 'tipo', label: 'Tipo ↕', width: 150 },
        {
            id: 'status',
            label: 'Status ↕',
            width: 150,
            getCellClass: (status) => {
                if (status === 'Aberto' || status === 'Disponível') return styles['status-verde'];
                if (status === 'Concluído') return styles['status-azul'];
                return '';
            }
        },
    ];

    const dadosTabela = [
        { id: '5524', nome: 'João Pereira', detalhe: 'Preciso de uma furadeira por 1...', tipo: 'Pedido', status: 'Aberto' },
        { id: '3392', nome: 'Maria Oliveira', detalhe: 'Escada disponível para emprést..', tipo: 'Objeto', status: 'Disponível' },
        { id: '3393', nome: 'Pedro Santos', detalhe: 'Posso ajudar com a mudança hoje', tipo: 'Interação', status: 'Concluído' },
        { id: '3394', nome: 'Ana Costa', detalhe: 'Preciso de ajuda com jardim...', tipo: 'Pedido', status: 'Aberto' },
        { id: '3395', nome: 'Carlos Lima', detalhe: 'Ferramentas emprestadas', tipo: 'Objeto', status: 'Disponível' },
        { id: '3395', nome: 'Carlos Lima', detalhe: 'Ferramentas emprestadas', tipo: 'Objeto', status: 'Disponível' },
        { id: '3395', nome: 'Carlos Lima', detalhe: 'Ferramentas emprestadas', tipo: 'Objeto', status: 'Disponível' },
        { id: '3395', nome: 'Carlos Lima', detalhe: 'Ferramentas emprestadas', tipo: 'Objeto', status: 'Disponível' },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log('Clicou na célula:', { valor, colunaId, linha });
    };

    return (
        <>
            <header className={styles.header}>
                <h1>Atividade Geral</h1>
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
                />
            </main>
        </>
    );
}

export default Activity;