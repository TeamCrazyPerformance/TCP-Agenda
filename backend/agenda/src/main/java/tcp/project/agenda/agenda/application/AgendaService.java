package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.exception.NoSuchGradeException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeRepository;
import tcp.project.agenda.member.domain.GradeType;
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
    private final GradeRepository gradeRepository;

    @Transactional
    public void createAgenda(Long memberId, AgendaCreateRequest request) {
        Member member = findMember(memberId);
        Grade target = findGrade(request);

        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), target, request.getClosedAt());
        List<AgendaItem> agendaItems = getAgendaItems(request, agenda);
        agenda.addAgendaItems(agendaItems);

        agendaRepository.save(agenda);
    }

    private static List<AgendaItem> getAgendaItems(AgendaCreateRequest request, Agenda agenda) {
        return request.getSelectList().stream()
                .map(agendaItemDto -> AgendaItem.createAgendaItem(agenda, agendaItemDto.getContent()))
                .collect(Collectors.toList());
    }

    private Grade findGrade(AgendaCreateRequest request) {
        return gradeRepository.findByGradeType(GradeType.from(request.getTarget()))
                .orElseThrow(() -> new NoSuchGradeException(request.getTarget()));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));
    }
}
