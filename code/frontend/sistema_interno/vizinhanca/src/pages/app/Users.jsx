import Searchbar from "../../components/ui/SearchBar";
import Table from "./components/Table";
import FilterOptions from "../../components/ui/Filter"
import styles from "./Users.module.css";

function Users() {

    const cards = [
        { id: 1, title: "Ativos", quantity: 128, color: "#10B765" },
        { id: 2, title: "Inativos", quantity: 12, color: "#A99817" },
        { id: 3, title: "Pendentes", quantity: 5, color: "#2EA9F5" },
        { id: 4, title: "Bloqueados", quantity: 2, color: "#FF1111" },
    ];

    const colunasTabela = [
        { id: 'id', label: 'ID', width: 100 },
        { id: 'nome', label: 'Nome ', width: 250 },
        { id: 'email', label: 'Email', width: 300 },
        { id: 'telefone', label: 'Telefone', width: 150 },
        {
            id: 'status',
            label: 'Status ',
            width: 150,
            getCellClass: (status) => {
                if (status === 'Ativo') return styles['status-verde'];
                if (status === 'Inativo') return styles['status-amarelo'];
                if (status === 'Pendente') return styles['status-azul'];
                if (status === 'Bloqueado') return styles['status-vermelho'];
                return '';
            }
        },
    ];

    const dadosTabela = [
        { id: '001', nome: 'João Pereira', email: 'joao.pereira@email.com', telefone: '(11) 98765-4321', status: 'Ativo' },
        { id: '002', nome: 'Maria Oliveira', email: 'maria.oliveira@email.com', telefone: '(11) 97654-3210', status: 'Ativo' },
        { id: '003', nome: 'Pedro Santos', email: 'pedro.santos@email.com', telefone: '(11) 96543-2109', status: 'Pendente' },
        { id: '004', nome: 'Ana Costa', email: 'ana.costa@email.com', telefone: '(11) 95432-1098', status: 'Ativo' },
        { id: '005', nome: 'Carlos Lima', email: 'carlos.lima@email.com', telefone: '(11) 94321-0987', status: 'Inativo' },
        { id: '006', nome: 'Fernanda Alves', email: 'fernanda.alves@email.com', telefone: '(11) 93210-9876', status: 'Ativo' },
        { id: '007', nome: 'Roberto Silva', email: 'roberto.silva@email.com', telefone: '(11) 92109-8765', status: 'Bloqueado' },
        { id: '008', nome: 'Juliana Mendes', email: 'juliana.mendes@email.com', telefone: '(11) 91098-7654', status: 'Ativo' },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        alert('Clicou na célula: ' + valor);
    };

    return (
        <>
            <header className={styles.header}>
                <h1>Lista de Moradores</h1>
            </header>

            <main className={styles.main}>
                <div className={styles.filterOptions}>
                <FilterOptions/>
                <Searchbar placeholder="Pesquisar por nome ou email" type="text"  />
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

export default Users;
