import Header from './Header';
import styles from './index.scss';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <div className={styles.container}>
      <Header />
      <div className={styles.contents}>{children}</div>
    </div>
  );
}

export default Layout;
