package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AgendaService {

    private final MemberRepository memberRepository;
    private final AgendaRepository agendaRepository;

    @Transactional
    public void createAgenda(Long memberId, AgendaCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));

        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), request.getTarget(), request.getClosedAt());
        List<AgendaItem> agendaItems = getAgendaItems(request, agenda);
        agenda.addAgendaItems(agendaItems);

        agendaRepository.save(agenda);
    }

    private static List<AgendaItem> getAgendaItems(AgendaCreateRequest request, Agenda agenda) {
        return request.getSelectList().stream()
                .map(agendaItemDto -> AgendaItem.createAgendaItem(agenda, agendaItemDto.getContent()))
                .collect(Collectors.toList());
    }
}
