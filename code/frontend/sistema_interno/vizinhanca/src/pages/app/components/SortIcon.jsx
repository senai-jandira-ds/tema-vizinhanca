import React from 'react';
import styles from './SortIcon.module.css';

export default function SortIcon({ direction }) {
  return (
    <svg
      className={`${styles.sortIcon} ${direction ? styles[direction] : ''}`}
      width="10"
      height="35"
      viewBox="0 0 10 17"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        className={styles.arrowUp}
        d="M0.750122 5.82617L4.04063 1.16054C4.22882 0.897539 4.48245 0.750135 4.74673 0.750135C5.011 0.750135 5.26464 0.897539 5.45283 1.16054L8.74333 5.82617"
        stroke="currentColor"
        strokeOpacity="0.6"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        className={styles.arrowDown}
        d="M0.750122 10.8262L4.04063 15.4918C4.22882 15.7548 4.48245 15.9022 4.74673 15.9022C5.011 15.9022 5.26464 15.7548 5.45283 15.4918L8.74333 10.8262"
        stroke="currentColor"
        strokeOpacity="0.6"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
