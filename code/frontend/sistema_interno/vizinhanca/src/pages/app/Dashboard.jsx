import { useState } from "react";
import Card from "../../components/ui/Card";
import { getCondominiumData } from "../../services/authService";
import { getMeCondominium  } from "../../services/condominiumService";
import { getActivities, formatActivityDate, formatActivityStatus, formatActivityType } from "../../services/activityService";
import Table from "./components/Table";
import styles from "./Dashboard.module.css";

function Dashboard() {

    const [showNotifications, setShowNotifications] = useState(false);

    const condominiumData = getCondominiumData()
    const fetchCondominium = getMeCondominium()

    const cards = [
        {
            id: 1,
            title: "Usuários",
            quantity: condominiumData.stats?.amount_residents || 0,
            color: "#10B765",
            to: "users"
        },
        {
            id: 2,
            title: "Pedidos",
            quantity: condominiumData.stats?.amount_services || 0,
            color: "#A99817",
            to: "services"
        },
        {
            id: 3,
            title: "Objetos",
            quantity: condominiumData.stats?.amount_objects || 0,
            color: "#2EA9F5",
            to: "services"
        },
        {
            id: 4,
            title: "Denúncias",
            quantity: condominiumData.stats?.amount_reports || 0,
            color: "#FF1111",
            to: "security"
        },
    ];

    const mappedData = condominiumData.activities.map((activity, index) => ({
        id: activity.resident_id.toString() || (index + 1).toString(),
        nome: activity.resident_name || '',
        detalhe: activity.description || '',
        tipo: formatActivityType(activity.type),
        status: formatActivityStatus(activity.status),
    }));

    const colunasTabela = [
        { id: 'id', label: 'Nº', width: 100 },
        { id: 'nome', label: 'Nome ', width: 200 },
        { id: 'detalhe', label: 'Detalhe', width: 350 },
        { id: 'tipo', label: 'Tipo ', width: 150 },
        {
            id: 'status',
            label: 'Status',
            width: 160,
            getCellClass: (status) => {
                if (status === 'Aberto' || status === 'Disponível') return styles['status-verde'];
                if (status === 'Concluido' || status === 'Finalizado') return styles['status-azul'];
                if (status === 'Pendente' || status === 'Indisponível' || status === 'Em andamento')return styles['status-amarelo'];
                return '';
            }
        },
    ];

    const dadosTabela = mappedData

    const handleCellClick = (valor, colunaId, linha) => {
        console.log('Clicou na célula:', { valor, colunaId, linha });
        // Aqui você pode adicionar lógica, como abrir um modal, navegar, etc.
    };

    return (
        <>
            <header className={styles.header}>
                <div className={styles.headerTop}>
                    <h1>Dashboard</h1>


                </div>
                <div className={styles.cardsList}>
                    {cards.map((card) => (
                        <Card
                            key={card.id}
                            title={card.title}
                            quantity={card.quantity}
                            color={card.color}
                            to={card.to}
                        />
                    ))}
                </div>
            </header>

            <main className={styles.main}>
                <h2>Atividade</h2>
                <Table
                    columns={colunasTabela}
                    data={dadosTabela}
                    onCellClick={handleCellClick}
                    showPagination={true}
                    showExport={false}
                    pageSize={4}
                />
            </main>
        </>
    );
}

export default Dashboard;