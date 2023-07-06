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
function AdminVote({ data }: VoteProps) {
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
            <div className={styles.end}>투표마감</div>
          )}
        </div>
      </div>

      <div className={styles.wrapVoteInfo}>
        <div className={styles.target}>대상 : {data.target}</div>
        <div>
          <img width="13" height="15" src={Person} alt="인원" />
          <div>투표 인원 : {data.votedMember} 명</div>
        </div>
        <div className={styles.calender}>
          <img width="13" height="15" src={Calender} alt="투표날짜" />
          <div>
            {createdAt} ~{closedAt}
          </div>
        </div>
      </div>
      <div className={styles.wrapButtons}>
        {data.isOpen ? (
          <div>
            <Button
              onClick={() => {
                navigate(`/vote/${data.id}`);
              }}
              className={styles.voteButton}
            >
              투표하기
            </Button>
            <Button
              onClick={() => {
                navigate(`/editvote/${data.id}`);
              }}
              className={styles.editButton}
            >
              수정하기
            </Button>
            <Button
              onClick={() => {
                navigate(`/closevote/${data.id}`);
              }}
              className={styles.closeButton}
            >
              마감하기
            </Button>
            <Button
              onClick={() => {
                navigate(`/deletevote/${data.id}`);
              }}
              className={styles.deleteButton}
            >
              삭제하기
            </Button>
          </div>
        ) : (
          <div>
            <Button disabled>투표하기</Button>
            <Button disabled>수정하기</Button>
            <Button disabled>마감하기</Button>
            <Button
              onClick={() => {
                navigate(`/deletevote/${data.id}`);
              }}
              className={styles.deleteButton}
            >
              삭제하기
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminVote;
