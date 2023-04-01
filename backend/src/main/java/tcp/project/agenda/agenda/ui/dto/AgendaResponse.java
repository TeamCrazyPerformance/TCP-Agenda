package tcp.project.agenda.agenda.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tcp.project.agenda.agenda.domain.Agenda;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AgendaResponse {
    private Long id;
    private String title;
    private String content;
    private String target;
    private int votedMember;
    private int totalMember;

    @JsonProperty("isOpen")
    private boolean open;
    private boolean voted;

    private List<SelectItemDto> selectList;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;

    public static AgendaResponse from(Agenda agenda, int votedMember, int totalMember, List<SelectItemDto> selectList, boolean voted) {
        return AgendaResponse.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .content(agenda.getContent())
                .target(agenda.getTarget().getCode())
                .votedMember(votedMember)
                .totalMember(totalMember)
                .open(!agenda.isClosed())
                .voted(voted)
                .selectList(selectList)
                .createdAt(agenda.getCreatedDate())
                .closedAt(agenda.getClosedAt())
                .build();
    }
}
