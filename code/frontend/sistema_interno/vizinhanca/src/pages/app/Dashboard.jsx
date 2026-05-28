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
    console.log(fetchCondominium)

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
                if (status === 'ABERTO' || status === 'DISPONÍVEL') return styles['status-verde'];
                if (status === 'CONCLUIDO' || status === 'FINALIZADO') return styles['status-azul'];
                if (status === 'PENDENTE' || status === 'INDISPONÍVEL') return styles['status-amarelo'];
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

                    <div className={styles.headerActions}>
                        <div className={styles.notifications}>

                            <svg
                                onClick={() => setShowNotifications(!showNotifications)}
                                viewBox="0 0 20 22"
                                fill="none"
                                xmlns="http://www.w3.org/2000/svg"
                            >

                                <path
                                    d="M9.75475 17.1716H17.1161C17.4027 17.1874 17.6888 17.1297 17.9469 17.0039C18.2049 16.878 18.4265 16.688 18.5903 16.4521C18.7541 16.2164 18.8548 15.9425 18.883 15.6569C18.9111 15.371 18.8654 15.0828 18.7506 14.8196C18.3624 13.6438 16.8103 12.2326 16.8103 10.8449C16.8103 7.76403 16.8103 6.95267 15.2934 5.14174C14.8015 4.55889 14.1925 4.08609 13.5058 3.75415L12.6592 3.34256C12.5168 3.25765 12.3938 3.14354 12.2986 3.00775C12.2034 2.87197 12.1379 2.71757 12.1065 2.55469C12.0221 2.00593 11.7322 1.50982 11.2954 1.16698C10.8587 0.824139 10.3079 0.660229 9.75475 0.708473C9.21126 0.675639 8.67515 0.846639 8.25099 1.18805C7.82715 1.52949 7.54556 2.01677 7.46174 2.55469C7.42375 2.72279 7.34917 2.88052 7.24351 3.01667C7.13786 3.15281 7.0036 3.26408 6.85012 3.34256L6.00352 3.75415C5.31694 4.08609 4.70791 4.55889 4.21611 5.14174C2.69916 6.95267 2.69917 7.76403 2.69917 10.8449C2.69917 12.2326 1.21749 13.5026 0.829445 14.7492C0.594253 15.5017 0.464901 17.1716 2.42869 17.1716H9.75475Z"
                                    stroke="black"
                                    strokeWidth="1.4"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />

                                <path
                                    d="M13.283 17.1719C13.2927 17.6379 13.2081 18.1009 13.034 18.5331C12.8602 18.9655 12.6008 19.3583 12.2712 19.6877C11.9418 20.0173 11.5489 20.2768 11.1166 20.4507C10.6843 20.6246 10.2214 20.7094 9.75537 20.6995C9.28938 20.7094 8.82647 20.6246 8.39402 20.4507C7.96171 20.2768 7.56893 20.0173 7.23936 19.6877C6.90994 19.3583 6.65039 18.9655 6.4765 18.5331C6.30261 18.1009 6.21793 17.6379 6.22754 17.1719"
                                    stroke="black"
                                    strokeWidth="1.4"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />

                            </svg>

                            {showNotifications && (

                                <div className={styles.notificationsDropdown}>

                                    <div className={styles.notificationItem}>
                                        Novo morador cadastrado
                                    </div>

                                    <div className={styles.notificationItem}>
                                        Nova denúncia enviada
                                    </div>

                                    <div className={styles.notificationItem}>
                                        Novo pedido publicado
                                    </div>

                                </div>

                            )}

                        </div>
                        <div className={styles.profile}></div>
                    </div>
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