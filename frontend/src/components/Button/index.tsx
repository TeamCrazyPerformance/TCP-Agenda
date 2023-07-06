import styles from './index.scss';

interface ButtonProps {
  children: React.ReactNode;
}

function Button({
  children,
  ...buttonAttribute
}: ButtonProps &
  React.DetailedHTMLProps<
    React.ButtonHTMLAttributes<HTMLButtonElement>,
    HTMLButtonElement
  >) {
  return (
    <>
      <button className={styles.button} {...buttonAttribute}>
        {children}
      </button>
    </>
  );
}

export default Button;
