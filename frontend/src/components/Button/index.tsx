import styles from './index.scss';

interface ButtonProps {
  children: React.ReactNode;
}

function Button({
  children,
}: ButtonProps &
  React.DetailedHTMLProps<
    React.ButtonHTMLAttributes<HTMLButtonElement>,
    HTMLButtonElement
  >) {
  return (
    <>
      <button className={styles.button}>{children}</button>
    </>
  );
}

export default Button;
