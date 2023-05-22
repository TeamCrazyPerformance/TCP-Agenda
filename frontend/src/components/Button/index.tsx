import styles from './index.scss';

interface ButtonProps {
  inputAttribute: React.ButtonHTMLAttributes<HTMLButtonElement>;
  children: React.ReactNode;
}

function Button({
  children,
  ...inputAttribute
}: ButtonProps &
  React.DetailedHTMLProps<
    React.ButtonHTMLAttributes<HTMLButtonElement>,
    HTMLButtonElement
  >) {
  return (
    <>
      <button className={styles.button} {...inputAttribute}>
        {children}
      </button>
    </>
  );
}

export default Button;
