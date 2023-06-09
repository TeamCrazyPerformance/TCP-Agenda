import Vote from 'components/Vote';
import styles from './index.scss';
import AdminVote from 'components/VoteAdmin';
function Home() {
  let data = {
    id: '1',
    title: 'test',
    createdAt: '2023-03-01T22:59:59',
    closedAt: '2023-03-01T22:59:59',
    target: '재학생',
    votedMember: '10',
    isOpen: true,
  };
  return (
    <div className={styles.container}>
      {/* <p>This is /</p> */}
      <AdminVote data={data} />
      {/* <Vote data={data} /> */}
    </div>
  );
}

export default Home;
