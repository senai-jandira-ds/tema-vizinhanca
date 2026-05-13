import InputCondominium from "./components/InputCondominium";
import VizinhancaLogo from "../../assets/images/VizinhancaLogo.png";
import { getCondominiumData } from "../../services/authService";
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

    const data = getCondominiumData()

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

                <div className={styles.condominiumInfo}>
                    <div className={styles.condominiumInfoForm}>
                        <span>Nome</span>
                        <InputCondominium value={data.name} />

                        <span>Endereço</span>
                        <InputCondominium
                            value={`${data.address.street}, ${data.address.number} - ${data.address.neighborhood}`}
                        />

                        <span>Cidade</span>
                        <InputCondominium value={data.address.city} />

                        <span>Estado</span>
                        <InputCondominium value={data.address.state} />

                        <span>Blocos no Condomínio</span>
                        <InputCondominium value={String(data.amount_blocks)} />

                        <span>Apartamentos Totais</span>
                        <InputCondominium value={String(data.amount_apartments)} />
                    </div>
                    <div className={styles.condominiumImage}>
                        <img src={VizinhancaLogo} alt="" />
                    </div>

                </div>

                <h2 className={styles.h2}>Atividade</h2>
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