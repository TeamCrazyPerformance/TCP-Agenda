package tcp.project.agenda.agenda.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.agenda.domain.Agenda;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AgendaResponse {
    private Long id;
    private String title;
    private String content;
    private String target;
    private int votedMember;
    private int totalMember;
    private List<SelectItemDto> selectList;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;

    public static AgendaResponse from(Agenda agenda, int votedMember, int totalMember, List<SelectItemDto> selectList) {
        return new AgendaResponse(agenda.getId(), agenda.getTitle(), agenda.getContent(), agenda.getTarget().getCode(), votedMember, totalMember, selectList, agenda.getCreatedDate(), agenda.getClosedAt());
    }
}
