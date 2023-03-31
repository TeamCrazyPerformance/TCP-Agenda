package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.member.domain.Member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "vote_list")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "agenda_item_id")
    private AgendaItem agendaItem;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    public Vote(Member member, AgendaItem agendaItem, Agenda agenda) {
        this.member = member;
        this.agendaItem = agendaItem;
        this.agenda = agenda;
    }

    public static Vote createVote(Member member, AgendaItem agendaItem, Agenda agenda) {
        Vote vote = new Vote(member, agendaItem, agenda);
        agenda.addVote(vote);
        return vote;
    }
}
