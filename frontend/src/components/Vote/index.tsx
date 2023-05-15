import styles from './index.scss';
import ConvertDate from '../../utils/convertDate';

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
  let createdAt = ConvertDate(data.createdAt);
  let closedAt = ConvertDate(data.closedAt);
  return (
    <div>
      <div className={styles.first}>
        <div>{data.title}</div>
        <div>{data.isOpen ? <div>진행중</div> : <div>투표마감</div>}</div>
      </div>
      <div className={styles.second}>대상 : {data.target}</div>
      <div className={styles.third}>
        <div>
          {createdAt} ~{closedAt}
        </div>
        <div>투표인원 : {data.votedMember} 명</div>
        {data.isOpen ? <button>투표하기</button> : <button>결과보기</button>}
      </div>
      {/* <input className={styles.input} {...inputAttribute}>
        {children}
      </input> */}
    </div>
  );
}

export default Vote;
