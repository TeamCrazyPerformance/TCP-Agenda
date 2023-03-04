package tcp.project.agenda.agenda.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaCreateRequest {

    private String title;
    private String content;
    private String target;
    private LocalDateTime closedAt;
    private List<AgendaItemDto> selectList;
}
