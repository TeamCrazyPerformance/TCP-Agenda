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
function AdminVote({ data }: VoteProps) {
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

      <div className={styles.second}>
        <div className={styles.target}>대상 : {data.target}</div>
        <div>
          <img width="13" height="15" src={Person} />
          <div>투표 인원 : {data.votedMember} 명</div>
        </div>
        <div className={styles.calender}>
          <img width="13" height="15" src={Calender} />
          <div>
            {createdAt} ~{closedAt}
          </div>
        </div>
      </div>
      <div className={styles.third}>
        {data.isOpen ? (
          <div>
            <Button
              inputAttribute={inputAttribute}
              className={styles.votebutton}
            >
              투표하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.editbutton}
            >
              수정하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.endbutton}
            >
              마감하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.deletebutton}
            >
              삭제하기
            </Button>
          </div>
        ) : (
          // <button className={styles.button}>투표하기</button>
          <div>
            <Button
              inputAttribute={inputAttribute}
              className={styles.votebutton}
            >
              투표하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.editbutton}
            >
              수정하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.endbutton}
            >
              마감하기
            </Button>
            <Button
              inputAttribute={inputAttribute}
              className={styles.deletebutton}
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
