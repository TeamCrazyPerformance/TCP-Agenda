import Header from './Header';
import styles from './index.scss';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <>
      <Header />
      <div className={styles.container}>
        <div className={styles.contents}>{children}</div>
      </div>
    </>
  );
}

export default Layout;
