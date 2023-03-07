package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidTitleException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.common.entity.BaseEntity;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.Member;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@Table(name = "agenda_list")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agenda extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private String content;

    @OneToOne
    @JoinColumn(name = "target_grade_id")
    private Grade target;

    private LocalDateTime closedAt;

    @Column(name = "is_closed")
    private boolean closed;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.PERSIST)
    private List<AgendaItem> agendaItems = new ArrayList<>();

    public Agenda(Member member, String title, String content, Grade target, LocalDateTime closedAt) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.target = target;
        this.closedAt = closedAt;
        this.closed = false;
    }

    public static Agenda createAgendaFrom(Member member, String title, String content, Grade target, LocalDateTime closedAt) {
        validateTitle(title);
        validateClosedAt(closedAt);
        content = Optional.ofNullable(content).orElse("");
        return new Agenda(member, title, content, target, closedAt);
    }

    private static void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new InvalidTitleException();
        }
    }

    private static void validateClosedAt(LocalDateTime closedAt) {
        if (LocalDateTime.now().isAfter(closedAt)) {
            throw new InvalidClosedAgendaTimeException();
        }
    }

    public void addAgendaItems(List<AgendaItem> agendaItems) {
        this.agendaItems.addAll(agendaItems);
    }

    public void validateOwner(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new NotAgendaOwnerException(id, memberId);
        }
    }

    public void close() {
        validateAlreadyClosed();
        this.closed = true;
    }

    public void validateAlreadyClosed() {
        if (isClosed()) {
            throw new AgendaAlreadyClosedException();
        }
    }
}
