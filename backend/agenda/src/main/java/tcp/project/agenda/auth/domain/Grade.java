package tcp.project.agenda.auth.domain;

public enum Grade {
    REPRESENTATIVE("감투"),
    REGULAR("정회원"),
    GENERAL("회원");

    private final String grade;

    Grade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }
}
