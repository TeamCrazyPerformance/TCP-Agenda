import { useNavigate } from 'react-router-dom';

import Button from 'components/Button';

import Calender from 'assets/svg/calender.svg';
import Person from 'assets/svg/person.svg';
import ConvertDate from 'utils/convertDate';

import styles from './index.scss';

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
  const navigate = useNavigate();
  return (
    <div className={styles.wrap}>
      <div className={styles.wrapTitle}>
        <div className={styles.title}>{data.title}</div>
        <div className={styles.voteState}>
          {data.isOpen ? (
            <div className={styles.process}>진행중</div>
          ) : (
            <div className={styles.end}>투표 마감</div>
          )}
        </div>
      </div>
      <div className={styles.wrapTargetPeople}>대상 : {data.target}</div>
      <div className={styles.wrapVoteInfo}>
        <div>
          <img width="13" height="15" src={Calender} alt="날짜" />
          <div>
            {createdAt} ~{closedAt}
          </div>
        </div>
        <div>
          <img width="13" height="15" src={Person} alt="인원" />
          <div>투표 인원 : {data.votedMember} 명</div>
        </div>
        {data.isOpen ? (
          <Button
            onClick={() => {
              navigate('/vote');
            }}
            className={styles.voteButton}
          >
            투표하기
          </Button>
        ) : (
          <Button
            onClick={() => {
              navigate(`/voteresult/${data.id}`);
            }}
            className={styles.voteResult}
          >
            결과보기
          </Button>
        )}
      </div>
    </div>
  );
}

export default Vote;
