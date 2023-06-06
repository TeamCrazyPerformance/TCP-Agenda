import styles from './index.scss';
import ConvertDate from '../../utils/convertDate';
import Button from 'components/Button';
import Calender from 'assets/svg/calender.svg';
import Person from 'assets/svg/person.svg';
interface VoteProps {
  data: {
    id: string;
    title: string;
    createdAt: string;
    closedAt: string;
    target: string;
    votedMember: string;
    isOpen: boolean;
  };
}
function Vote({ data }: VoteProps) {
  const createdAt = ConvertDate(data.createdAt);
  const closedAt = ConvertDate(data.closedAt);
  const inputAttribute = {
    onClick: () => {
      console.log('Button clicked!');
    },
    disabled: true,
  };
  return (
    <div className={styles.wrap}>
      <div className={styles.first}>
        <div className={styles.title}>{data.title}</div>
        <div className={styles.voteState}>
          {data.isOpen ? (
            <div className={styles.process}>진행중</div>
          ) : (
            <div className={styles.end}>투표마감</div>
          )}
        </div>
      </div>
      <div className={styles.second}>대상 : {data.target}</div>
      <div className={styles.third}>
        <img width="13" height="15" src={Calender} />
        <div>
          {createdAt} ~{closedAt}
        </div>
        <img width="13" height="15" src={Person} />
        <div>투표 인원 : {data.votedMember} 명</div>
        {data.isOpen ? (
          <Button inputAttribute={inputAttribute} className={styles.votebutton}>
            투표하기
          </Button>
        ) : (
          // <button className={styles.button}>투표하기</button>
          <Button inputAttribute={styles.voteresult}>결과보기</Button>
        )}
      </div>
    </div>
  );
}

export default Vote;
