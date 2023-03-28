import styles from './index.scss';

interface InputProps {
  children: React.ReactNode;
}
function Input({
  children,
  ...inputAttribute
}: InputProps &
  React.DetailedHTMLProps<
    React.InputHTMLAttributes<HTMLInputElement>,
    HTMLInputElement
  >) {
  return (
    <div>
      <input className={styles.input} {...inputAttribute}>
        {children}
      </input>
    </div>
  );
}

export default Input;
