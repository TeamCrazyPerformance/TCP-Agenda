package tcp.project.agenda.member.domain;

import lombok.Getter;
import tcp.project.agenda.auth.exception.NoSuchGradeException;

import java.util.Arrays;

@Getter
public enum GradeType {
    EXECUTIVE("감투"),
    REGULAR("정회원"),
    GENERAL("회원");

    private final String code;

    GradeType(String code) {
        this.code = code;
    }

    public static GradeType from(String code) {
        return Arrays.stream(GradeType.values())
                .filter(gradeType -> gradeType.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new NoSuchGradeException(code));
    }
}
