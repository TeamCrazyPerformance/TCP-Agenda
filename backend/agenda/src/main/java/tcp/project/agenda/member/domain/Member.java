package tcp.project.agenda.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.common.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public Member(String name, String univId, String password) {
        this.name = name;
        this.univId = univId;
        this.password = password;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}
