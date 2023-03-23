import styles from './index.scss';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return <div className={styles.container}>{children}</div>;
}

export default Layout;
