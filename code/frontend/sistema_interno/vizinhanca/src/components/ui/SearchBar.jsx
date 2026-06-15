import styles from './SearchBar.module.css'

export default function SearchBar(props) {
    return (
      <input {...props} className={styles.input} />
    )
  }