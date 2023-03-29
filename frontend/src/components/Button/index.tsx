import styles from './index.scss';

interface ButtonProps {
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
