package tcp.project.agenda.agenda.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tcp.project.agenda.agenda.application.AgendaService;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.auth.ui.Authenticated;

@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping("")
    public ResponseEntity<Void> createAgenda(@Authenticated Long memberId, @RequestBody AgendaCreateRequest request) {
        agendaService.createAgenda(memberId, request);
        return ResponseEntity.ok().build();
    }
}