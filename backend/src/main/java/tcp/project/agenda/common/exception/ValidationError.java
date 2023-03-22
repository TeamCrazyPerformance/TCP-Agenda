package tcp.project.agenda.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ValidationError {

    private final String name;
    private final String code;

    public static ValidationError of(String name, String code) {
        return new ValidationError(name, code);
    }
}
