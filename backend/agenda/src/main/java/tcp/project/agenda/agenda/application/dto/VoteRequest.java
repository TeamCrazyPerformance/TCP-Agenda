package tcp.project.agenda.agenda.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    private List<SelectedAgendaItemDto> selectList;
}
