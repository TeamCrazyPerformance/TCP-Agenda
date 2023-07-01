import styles from './index.scss';

function Home() {
  const data = {
    id: '1',
    title: 'test',
    createdAt: '2023-03-01T22:59:59',
    closedAt: '2023-03-01T22:59:59',
    target: '재학생',
    votedMember: '10',
    isOpen: true,
  };
  return <div className={styles.container}></div>;
}

export default Home;
