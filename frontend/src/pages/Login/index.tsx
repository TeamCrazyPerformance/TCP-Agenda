import { useRef } from 'react';

import Button from 'components/Button';
import Input from 'components/Input';

import KeySVG from 'assets/svg/key.svg';
import PersonSVG from 'assets/svg/person.svg';

import styles from './index.scss';

function Login() {
  const idRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);

  return (
    <div className={styles.container}>
      <div className={styles.inputContainer}>
        <div className={styles.singleLineInput}>
          <img src={PersonSVG} alt="" />
          <Input
            type="text"
            placeholder="Username"
            ref={idRef}
            maxLength={20}
            required
          />
        </div>
        <div className={styles.singleLineInput}>
          <img src={KeySVG} alt="" />
          <Input
            type="password"
            placeholder="····"
            ref={passwordRef}
            maxLength={255}
            required
          />
        </div>
      </div>
      {/* <Button className={styles.submitButton}>로그인</Button> */}
    </div>
  );
}

export default Login;
