import { Link } from "react-router-dom";

import styles from "./Card.module.css";
import Back from "../../assets/icons/backicon.svg?react";

function Card({ title, quantity, color, to }) {

    return (

            <Link to={to} className={styles.card}>

                <div className={styles.leftContent}>
                    <span style={{ color }}>{title}</span>
                    <span>{quantity}</span>
                </div>

                <div className={styles.rightContent}>
                    <Back className={styles.icon} />
                </div>

            </Link>
    );
}

export default Card;