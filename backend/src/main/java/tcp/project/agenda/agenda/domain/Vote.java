package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.member.domain.Member;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Getter
@Entity
@Table(name = "votes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_item_id")
    private AgendaItem agendaItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    private Vote(Member member, AgendaItem agendaItem, Agenda agenda) {
        this.member = member;
        this.agendaItem = agendaItem;
        this.agenda = agenda;
    }

    public static Vote createVote(Member member, AgendaItem agendaItem, Agenda agenda) {
        Vote vote = new Vote(member, agendaItem, agenda);
        agenda.addVote(vote);
        agendaItem.addVote(vote);
        return vote;
    }

    public static boolean didMemberVote(List<Vote> votes, Long memberId) {
        return votes.stream()
                .anyMatch(vote -> vote.getMember().getId().equals(memberId));
    }
}
