import styles from './App.module.css';

/** Pantalla inicial del esqueleto: comprueba que el panel de administración arranca. */
export function App() {
  return (
    <main className={styles.page}>
      <h1 className={styles.title}>WEather</h1>
      <p className={styles.subtitle}>Panel de administración.</p>
    </main>
  );
}
