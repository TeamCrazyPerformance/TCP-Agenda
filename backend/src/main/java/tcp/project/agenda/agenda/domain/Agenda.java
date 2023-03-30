package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.agenda.exception.AgendaAlreadyClosedException;
import tcp.project.agenda.agenda.exception.InvalidClosedAgendaTimeException;
import tcp.project.agenda.agenda.exception.InvalidUpdateAlreadyVoteStartedAgendaException;
import tcp.project.agenda.agenda.exception.NotAgendaOwnerException;
import tcp.project.agenda.agenda.exception.NotTargetMemberException;
import tcp.project.agenda.common.entity.BaseEntity;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.Member;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @Enumerated(EnumType.STRING)
    private GradeType target;

    private LocalDateTime closedAt;

    @Column(name = "is_closed")
    private boolean closed;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AgendaItem> agendaItems = new ArrayList<>();

    @OneToMany(mappedBy = "agenda")
    private final List<Vote> votes = new ArrayList<>();

    public Agenda(Member member, String title, String content, GradeType target, LocalDateTime closedAt) {
        validateClosedAt(closedAt);
        content = Optional.ofNullable(content).orElse("");
        this.member = member;
        this.title = title;
        this.content = content;
        this.target = target;
        this.closedAt = closedAt;
        this.closed = false;
    }

    public static Agenda createAgendaFrom(Member member, String title, String content, GradeType target, LocalDateTime closedAt) {
        return new Agenda(member, title, content, target, closedAt);
    }

    private static void validateClosedAt(LocalDateTime closedAt) {
        if (LocalDateTime.now().isAfter(closedAt)) {
            throw new InvalidClosedAgendaTimeException();
        }
    }

    public void updateAgendaItems(List<AgendaItem> agendaItems) {
        this.agendaItems.clear();
        agendaItems.forEach(agendaItem -> agendaItem.setAgenda(this));
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

    public void validateIsTargetGrade(List<Grade> grades) {
        grades.stream()
                .filter(grade -> this.target.equals(grade.getGradeType()))
                .findAny()
                .orElseThrow(NotTargetMemberException::new);
    }

    public void update(String title, String content, LocalDateTime closedAt, String target) {
        validateAlreadyClosed();
        validateClosedAt(closedAt);
        if (isVoteStarted()) {
            validateUpdatingAlreadyStartedVote(title, content, target);
        }
        this.title = title;
        this.content = content;
        this.closedAt = closedAt;
        this.target = GradeType.from(target);
    }

    private void validateUpdatingAlreadyStartedVote(String title, String content, String target) {
        if (isChanged(this.title, title) || isChanged(this.content, content) || isChanged(this.target, GradeType.from(target))) {
            throw new InvalidUpdateAlreadyVoteStartedAgendaException();
        }
    }

    private <T> boolean isChanged(T original, T current) {
        return !original.equals(current);
    }

    private boolean isVoteStarted() {
        return !votes.isEmpty();
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }
}
