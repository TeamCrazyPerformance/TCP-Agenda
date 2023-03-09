package tcp.project.agenda.common.fixture;

import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaItemDto;
import tcp.project.agenda.agenda.application.dto.SelectedAgendaItemDto;
import tcp.project.agenda.agenda.application.dto.VoteRequest;

import java.time.LocalDateTime;
import java.util.List;

public class AgendaFixture {

    public static final String BASIC_AGENDA_TITLE = "아젠다 제목";
    public static final String BASIC_AGENDA_CONTENT = "아젠다 내용";
    public static final String BASIC_AGENDA_TARGET = "정회원";
    public static final LocalDateTime BASIC_AGENDA_CLOSED_AT = LocalDateTime.now().plusDays(5);
    public static final String BASIC_AGENDA_ITEM1 = "투표1";
    public static final String BASIC_AGENDA_ITEM2 = "투표2";
    public static final List<AgendaItemDto> BASIC_AGENDA_SELECTED_LIST_DTO = List.of(new AgendaItemDto(BASIC_AGENDA_ITEM1), new AgendaItemDto(BASIC_AGENDA_ITEM2));

    public static AgendaCreateRequest getBasicAgendaCreateRequest() {
        return new AgendaCreateRequest(BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, BASIC_AGENDA_TARGET, BASIC_AGENDA_CLOSED_AT, BASIC_AGENDA_SELECTED_LIST_DTO);
    }

    public static AgendaCreateRequest getInvalidAgendaCreateRequest() {
        return new AgendaCreateRequest(null, null, null, null, null);
    }
    
    public static AgendaCreateRequest getInvalidClosedAtAgendaCreateRequest() {
        return new AgendaCreateRequest(BASIC_AGENDA_TITLE, BASIC_AGENDA_CONTENT, BASIC_AGENDA_TARGET, LocalDateTime.now(), BASIC_AGENDA_SELECTED_LIST_DTO);
    }

    public static VoteRequest getBasicVoteRequest() {
        return new VoteRequest(List.of(new SelectedAgendaItemDto(1L), new SelectedAgendaItemDto(2L)));
    }

    public static VoteRequest getNotExistSelectItemVoteRequest() {
        return new VoteRequest(List.of(new SelectedAgendaItemDto(9999L)));
    }
}
