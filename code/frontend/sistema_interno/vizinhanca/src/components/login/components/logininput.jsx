import styles from '../login/logininput.module.css'

export default function LoginInput(props) {
    return (
      <input {...props} className={styles.input} />
    )
  }
  