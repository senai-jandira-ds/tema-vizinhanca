import Card from "./components/card";
import Table from "./components/table";
import styles from "./style.module.css";

function Dashboard() {

    const cards = [
        { id: 1, title: "Usuários", quantity: 10, color: "#10B765" },
        { id: 2, title: "Pedidos", quantity: 5, color: "#A99817" },
        { id: 3, title: "Objetos", quantity: 2, color: "#2EA9F5" },
        { id: 4, title: "Denúncias", quantity: 1, color: "#FF1111" },
    ];

    return (
        <>
            <header>
                <h1>Dashboard</h1>

                <div className={styles.cardsList}>
                    {cards.map((card) => (
                        <Card
                            key={card.id}
                            title={card.title}
                            quantity={card.quantity}
                            color={card.color}
                        />
                    ))}
                </div>
            </header>

            <main>
                <h2>Atividade</h2>
                <Table />
            </main>
        </>
    );
}

export default Dashboard;