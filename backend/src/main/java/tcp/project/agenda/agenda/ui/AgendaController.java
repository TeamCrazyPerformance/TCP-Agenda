package tcp.project.agenda.agenda.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tcp.project.agenda.agenda.application.AgendaService;
import tcp.project.agenda.agenda.application.VoteService;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.application.dto.VoteRequest;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.agenda.ui.dto.AgendaResponse;
import tcp.project.agenda.auth.ui.Authenticated;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final VoteService voteService;

    @PostMapping("")
    public ResponseEntity<Void> createAgenda(@Authenticated Long memberId, @RequestBody AgendaCreateRequest request) {
        agendaService.createAgenda(memberId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{agendaId}")
    public ResponseEntity<Void> closeAgenda(@Authenticated Long memberId, @PathVariable Long agendaId) {
        agendaService.closeAgenda(memberId, agendaId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{agendaId}/vote")
    public ResponseEntity<Void> vote(@Authenticated Long memberId, @PathVariable Long agendaId, @RequestBody VoteRequest request) {
        voteService.vote(memberId, agendaId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{agendaId}/cancel")
    public ResponseEntity<Void> cancelVote(@Authenticated Long memberId, @PathVariable Long agendaId) {
        voteService.cancelVote(memberId, agendaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<AgendaListResponse> getAgendaList(@Authenticated Long memberId, @PageableDefault Pageable pageable) {
        AgendaListResponse response = agendaService.getAgendaList(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{agendaId}")
    public ResponseEntity<AgendaResponse> getAgenda(@Authenticated Long memberId, @PathVariable Long agendaId) {
        AgendaResponse response = agendaService.getAgenda(agendaId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{agendaId}")
    public ResponseEntity<Void> deleteAgenda(@Authenticated Long memberId, @PathVariable Long agendaId) {
        agendaService.deleteAgenda(memberId, agendaId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{agendaId}")
    public ResponseEntity<Void> updateAgenda(@Authenticated Long memberId, @PathVariable Long agendaId, @RequestBody AgendaUpdateRequest request) {
        agendaService.updateAgenda(memberId, agendaId, request);
        return ResponseEntity.ok().build();
    }
}
