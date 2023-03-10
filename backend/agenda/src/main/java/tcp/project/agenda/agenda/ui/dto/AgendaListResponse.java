package tcp.project.agenda.agenda.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaListResponse {
    private List<AgendaDto> agendaList;
    private int pageNumber;
    private boolean hasNext;
}
