package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaItemDto;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.application.dto.SelectedAgendaItemDto;
import tcp.project.agenda.agenda.application.dto.VoteRequest;
import tcp.project.agenda.agenda.application.validator.AgendaCreateValidator;
import tcp.project.agenda.agenda.application.validator.AgendaUpdateValidator;
import tcp.project.agenda.agenda.application.validator.VoteValidator;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaItemNotFoundException;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.exception.AlreadyVoteException;
import tcp.project.agenda.agenda.ui.dto.AgendaDto;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.agenda.ui.dto.AgendaResponse;
import tcp.project.agenda.agenda.ui.dto.SelectItemDto;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.exception.NoSuchGradeException;
import tcp.project.agenda.common.exception.AgendaException;
import tcp.project.agenda.common.exception.ValidationError;
import tcp.project.agenda.common.exception.ValidationException;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeRepository;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberGradeRepository;
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
    private final MemberGradeRepository memberGradeRepository;

    @Transactional
    public void createAgenda(Long memberId, AgendaCreateRequest request) {
        validateAgendaCreateRequest(request);
        Member member = findMember(memberId);

        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), GradeType.from(request.getTarget()), request.getClosedAt());
        List<AgendaItem> agendaItems = getAgendaItems(request.getSelectList(), agenda);
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

    private List<AgendaItem> getAgendaItems(List<AgendaItemDto> selectItemList, Agenda agenda) {
        return selectItemList.stream()
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

        List<Long> selectItemIdList = getSelectItemIdList(request);
        List<Long> agendaItemIdList = getAgendaItemIdList(agenda);

        validateVote(agenda, member, selectItemIdList, agendaItemIdList);

        List<AgendaItem> selectedAgendaItems = agendaItemRepository.findByIdIn(selectItemIdList);
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

    private List<Long> getSelectItemIdList(VoteRequest request) {
        return request.getSelectList().stream()
                .map(SelectedAgendaItemDto::getId)
                .collect(Collectors.toList());
    }

    private List<Long> getAgendaItemIdList(Agenda agenda) {
        return agenda.getAgendaItems().stream()
                .map(AgendaItem::getId)
                .collect(Collectors.toList());
    }


    private void validateVote(Agenda agenda, Member member, List<Long> selectItemIdList, List<Long> agendaItemIdList) {
        agenda.validateAlreadyClosed();
        agenda.validateIsTargetGrade(member.getGrades());
        validateAlreadyVote(member.getId(), agenda.getId());
        validateExistAgendaItem(agendaItemIdList, selectItemIdList);
    }

    private void validateAlreadyVote(Long memberId, Long agendaId) {
        if (voteRepository.existsByMemberIdAndAgendaId(memberId, agendaId)) {
            throw new AlreadyVoteException(agendaId);
        }
    }

    private void validateExistAgendaItem(List<Long> agendaItemIdList, List<Long> selectItemIdList) {
        selectItemIdList.stream()
                .filter(voteId -> !agendaItemIdList.contains(voteId))
                .findAny()
                .ifPresent(voteId -> {
                    throw new AgendaItemNotFoundException(voteId);
                });
    }

    @Transactional
    public void cancelVote(Long memberId, Long agendaId) {
        List<Vote> votes = voteRepository.findByMemberIdAndAgendaId(memberId, agendaId);
        voteRepository.deleteAllInBatch(votes);
    }

    public AgendaResponse getAgenda(Long agendaId) {
        Agenda agenda = findAgenda(agendaId);

        List<AgendaItem> agendaItems = agenda.getAgendaItems();
        List<SelectItemDto> selectList = agendaItems.stream()
                .map(agendaItem -> new SelectItemDto(agendaItem.getId(), agendaItem.getContent(), agendaItem.getVoteCount()))
                .collect(Collectors.toList());

        GradeType targetGradeType = agenda.getTarget();
        int totalMember = memberGradeRepository.countByGrade_GradeType(targetGradeType);
        int votedMember = voteRepository.countDistinctMember(agenda);

        return AgendaResponse.from(agenda, votedMember, totalMember, selectList);
    }

    @Transactional
    public void deleteAgenda(Long memberId, Long agendaId) {
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);

        List<Vote> votes = voteRepository.findByAgendaId(agendaId);
        if (!votes.isEmpty()) {
            voteRepository.deleteAllInBatch(votes);
        }
        agendaRepository.delete(agenda);
    }

    @Transactional
    public void updateAgenda(Long memberId, Long agendaId, AgendaUpdateRequest request) {
        validateAgendaUpdateRequest(request);
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);

        List<AgendaItem> agendaItems = getAgendaItems(request.getSelectList(), agenda);
        agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget(), agendaItems);
    }

    private void validateAgendaUpdateRequest(AgendaUpdateRequest request) {
        AgendaUpdateValidator validator = new AgendaUpdateValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
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
