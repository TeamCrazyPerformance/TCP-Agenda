import styles from './index.scss';

interface InputProps {
  children?: React.ReactNode;
}

function Input({
  ...inputAttribute
}: InputProps &
  React.DetailedHTMLProps<
    React.InputHTMLAttributes<HTMLInputElement>,
    HTMLInputElement
  >) {
  return <input className={styles.input} {...inputAttribute} />;
}

export default Input;
