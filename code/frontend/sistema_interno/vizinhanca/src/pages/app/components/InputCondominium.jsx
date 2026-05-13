import styles from "./InputCondominium.module.css";

export default function Input({
  placeholder = "Digite algo...",
  value,
  onChange,
}) {
  return (
    <div className={styles.inputWrapper}>
      <input
        className={styles["custom-input"]}
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
    </div>
  );
}