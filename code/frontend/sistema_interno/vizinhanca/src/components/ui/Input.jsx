import styles from './Input.module.css'

export default function Input({ style, ...props }) {
  return (
    <input
      style={style}
      {...props}
      className={styles.input}
    />
  )
}