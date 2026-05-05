import styles from "./Card.module.css";
import Back from "../../assets/icons/backicon.svg?react";

function Card({ title, quantity, color }) {

    return (
        <>
            <div className={styles.card}>
                <div className={styles.leftContent}>
                    <span style={{ color: color }}>{title}</span>
                    <span>{quantity}</span>
                </div>
                <div className={styles.rightContent}>
                    <Back className={styles.icon} />
                </div>
            </div>
        </>
    )
}

export default Card;