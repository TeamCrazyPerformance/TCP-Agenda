package tcp.project.agenda.agenda.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "id")
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

    public AgendaItem(String content) {
        this.content = content;
    }

    public static AgendaItem createAgendaItem(String content) {
        return new AgendaItem(content);
    }

    public void mappingAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public int getVoteCount() {
        return votes.size();
    }
}
