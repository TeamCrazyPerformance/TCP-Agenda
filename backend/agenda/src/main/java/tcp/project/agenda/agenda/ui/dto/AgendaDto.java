package tcp.project.agenda.agenda.ui.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgendaDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String target;
    private int votedMember;
    @JsonProperty("isOpen")
    private boolean open;
}
