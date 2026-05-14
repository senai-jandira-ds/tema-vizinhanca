import InputCondominium from "./components/InputCondominium";
import Table from "./components/Table";

import { getCondominiumData } from "../../services/authService";

import styles from "./Information.module.css";
import settingsStyles from "./Settings.module.css";

function Informacoes() {

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
                    <img
                        src={data.photo || ""}
                        alt="Logo do Condomínio"
                    />
                </div>

            </div>

            <h2 className={styles.title}>
                Atividade
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

export default Informacoes;