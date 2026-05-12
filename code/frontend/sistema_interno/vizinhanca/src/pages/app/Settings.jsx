import InputCondominium from "./components/InputCondominium";
import Table from "./components/Table";
import styles from "./Settings.module.css";

function Settings() {

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ', width: 200 },
        { id: 'detalhe', label: 'Detalhe', width: 350 },
        { id: 'tipo', label: 'Tipo ', width: 150 },
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
        { id: '5524', nome: 'João Pereira', detalhe: 'Preciso de uma furadeira por 1...', tipo: 'Pedido', status: 'Aberto' },
        { id: '3392', nome: 'Maria Oliveira', detalhe: 'Escada disponível para emprést..', tipo: 'Objeto', status: 'Disponível' },
        { id: '3393', nome: 'Pedro Santos', detalhe: 'Posso ajudar com a mudança hoje', tipo: 'Interação', status: 'Concluído' },
        { id: '3394', nome: 'Ana Costa', detalhe: 'Preciso de ajuda com jardim...', tipo: 'Pedido', status: 'Aberto' },
        { id: '3395', nome: 'Carlos Lima', detalhe: 'Ferramentas emprestadas', tipo: 'Objeto', status: 'Disponível' },
    ];

    const handleCellClick = (valor, colunaId, linha) => {
        console.log('Clicou na célula:', { valor, colunaId, linha });
        // Aqui você pode adicionar lógica, como abrir um modal, navegar, etc.
    };

    return (
        <>
            <header className={styles.header}>
                <h1>Configurações do Condomínio</h1>

            </header>
            <div className={styles.nav}>
                <a href="">Informações</a>
                <a href="">Categoria</a>
            </div>
            <main className={styles.main}>

                <div className="condominium-info">
                    <span></span>
                    <InputCondominium />
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                </div>

                <h2>Atividade</h2>
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

export default Settings;