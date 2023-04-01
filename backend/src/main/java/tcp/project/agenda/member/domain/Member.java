package tcp.project.agenda.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.common.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String univId;

    private String password;

    @OneToMany(mappedBy = "member")
    private final List<MemberGrade> grades = new ArrayList<>();

    public Member(String name, String univId, String password) {
        this.name = name;
        this.univId = univId;
        this.password = password;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public List<GradeType> getGrades() {
        return grades.stream()
                .map(MemberGrade::getGrade)
                .map(Grade::getGradeType)
                .collect(Collectors.toList());
    }
}
