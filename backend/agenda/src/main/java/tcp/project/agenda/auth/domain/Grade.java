package tcp.project.agenda.auth.domain;

import lombok.Getter;
import tcp.project.agenda.auth.exception.NoSuchGradeException;

import java.util.Arrays;

@Getter
public enum Grade {
    REPRESENTATIVE("감투"),
    REGULAR("정회원"),
    GENERAL("회원");

    private final String code;

    Grade(String code) {
        this.code = code;
    }

    public static Grade from(String code) {
        return Arrays.stream(Grade.values())
                .filter(grade -> grade.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new NoSuchGradeException(code));
    }
}
