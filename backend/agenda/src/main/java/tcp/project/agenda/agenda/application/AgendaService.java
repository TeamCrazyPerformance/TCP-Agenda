package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.SelectedAgendaItemDto;
import tcp.project.agenda.agenda.application.dto.VoteRequest;
import tcp.project.agenda.agenda.application.validator.AgendaCreateValidator;
import tcp.project.agenda.agenda.application.validator.VoteValidator;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.ui.dto.AgendaDto;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.exception.NoSuchGradeException;
import tcp.project.agenda.common.exception.ValidationError;
import tcp.project.agenda.common.exception.ValidationException;
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
    private final AgendaItemRepository agendaItemRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void createAgenda(Long memberId, AgendaCreateRequest request) {
        validateAgendaCreateRequest(request);
        Member member = findMember(memberId);
        Grade target = findGrade(request);

        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), target, request.getClosedAt());
        List<AgendaItem> agendaItems = getAgendaItems(request, agenda);
        agenda.addAgendaItems(agendaItems);

        agendaRepository.save(agenda);
    }

    private void validateAgendaCreateRequest(AgendaCreateRequest request) {
        AgendaCreateValidator validator = new AgendaCreateValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<AgendaItem> getAgendaItems(AgendaCreateRequest request, Agenda agenda) {
        return request.getSelectList().stream()
                .map(agendaItemDto -> AgendaItem.createAgendaItem(agenda, agendaItemDto.getContent()))
                .collect(Collectors.toList());
    }

    public AgendaListResponse getAgendaList(Pageable pageable) {
        Slice<Agenda> agendaPage = agendaRepository.findAll(pageable);
        List<AgendaDto> agendaList = getAgendaDtoList(agendaPage);
        return new AgendaListResponse(agendaList, agendaPage.getNumber(), agendaPage.hasNext());
    }

    private List<AgendaDto> getAgendaDtoList(Slice<Agenda> agendaPage) {
        return agendaPage.getContent().stream()
                .map(agenda -> new AgendaDto(agenda.getId(),
                        agenda.getTitle(),
                        agenda.getTarget().getCode(),
                        voteRepository.countDistinctMember(agenda),
                        agenda.getCreatedDate(),
                        agenda.getClosedAt(),
                        !agenda.isClosed()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void closeAgenda(Long memberId, Long agendaId) {
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);
        agenda.validateAlreadyClosed();

        agenda.close();
    }

    @Transactional
    public void vote(Long memberId, Long agendaId, VoteRequest request) {
        validateVoteRequest(request);
        Agenda agenda = findAgenda(agendaId);
        Member member = findMember(memberId);
        agenda.validateAlreadyClosed();
        agenda.validateIsTargetGrade(member.getGrades());

        List<Long> agendaItemIdList = getAgendaItemIdList(agenda);

        List<Long> idList = getIdList(request);
        validateExistAgendaItem(agendaItemIdList, idList);
        List<AgendaItem> selectedAgendaItems = agendaItemRepository.findByIdIn(idList);

        selectedAgendaItems.stream()
                .map(agendaItem -> Vote.createVote(member, agendaItem, agenda))
                .forEach(voteRepository::save);
    }

    private void validateVoteRequest(VoteRequest request) {
        VoteValidator validator = new VoteValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<Long> getAgendaItemIdList(Agenda agenda) {
        return agenda.getAgendaItems().stream()
                .map(AgendaItem::getId)
                .collect(Collectors.toList());
    }

    private void validateExistAgendaItem(List<Long> agendaItemIdList, List<Long> idList) {
        idList.stream()
                .filter(voteId -> !agendaItemIdList.contains(voteId))
                .findAny()
                .ifPresent(voteId -> {
                    throw new AgendaItemNotFoundException(voteId);
                });
    }

    private List<Long> getIdList(VoteRequest request) {
        return request.getSelectList().stream()
                .map(SelectedAgendaItemDto::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelVote(Long memberId, Long agendaId) {
        List<Vote> votes = voteRepository.findByMember_IdAndAgenda_Id(memberId, agendaId);
        voteRepository.deleteAllInBatch(votes);
    }

    private Agenda findAgenda(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException(agendaId));
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
