package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    private String content;

    @OneToMany(mappedBy = "agendaItem")
    private List<Vote> votes;

    public AgendaItem(Agenda agenda, String content) {
        this.agenda = agenda;
        this.content = content;
    }

    public static AgendaItem createAgendaItem(Agenda agenda, String content) {
        return new AgendaItem(agenda, content);
    }

    public int getVoteCount() {
        return votes.size();
    }
}
