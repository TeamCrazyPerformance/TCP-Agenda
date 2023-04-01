package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.AgendaCreateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaItemDto;
import tcp.project.agenda.agenda.application.dto.AgendaItemUpdateRequest;
import tcp.project.agenda.agenda.application.dto.AgendaUpdateRequest;
import tcp.project.agenda.agenda.application.validator.AgendaCreateValidator;
import tcp.project.agenda.agenda.application.validator.AgendaItemUpdateValidator;
import tcp.project.agenda.agenda.application.validator.AgendaUpdateValidator;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaItemRepository;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.agenda.exception.AgendaNotFoundException;
import tcp.project.agenda.agenda.ui.dto.AgendaDto;
import tcp.project.agenda.agenda.ui.dto.AgendaListResponse;
import tcp.project.agenda.agenda.ui.dto.AgendaResponse;
import tcp.project.agenda.agenda.ui.dto.SelectItemDto;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.common.exception.ValidationError;
import tcp.project.agenda.common.exception.ValidationException;
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
    private final VoteRepository voteRepository;
    private final MemberGradeRepository memberGradeRepository;
    private final AgendaItemRepository agendaItemRepository;

    @Transactional
    public void createAgenda(Long memberId, AgendaCreateRequest request) {
        validateAgendaCreateRequest(request);
        Member member = findMember(memberId);

        List<AgendaItem> agendaItems = getAgendaItems(request.getSelectList());
        Agenda agenda = Agenda.createAgendaFrom(member, request.getTitle(), request.getContent(), GradeType.from(request.getTarget()), request.getClosedAt());
        agenda.updateAgendaItems(agendaItems);

        agendaRepository.save(agenda);
    }

    private void validateAgendaCreateRequest(AgendaCreateRequest request) {
        AgendaCreateValidator validator = new AgendaCreateValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<AgendaItem> getAgendaItems(List<AgendaItemDto> selectItemList) {
        return selectItemList.stream()
                .map(agendaItemDto -> AgendaItem.createAgendaItem(agendaItemDto.getContent()))
                .collect(Collectors.toList());
    }

    public AgendaListResponse getAgendaList(Pageable pageable) {
        Slice<Agenda> agendaPage = agendaRepository.findSliceBy(pageable);
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

        agenda.close();
    }

    public AgendaResponse getAgenda(Long memberId, Long agendaId) {
        Agenda agenda = findAgenda(agendaId);

        List<SelectItemDto> selectList = agenda.getAgendaItems().stream()
                .map(agendaItem -> new SelectItemDto(agendaItem.getId(), agendaItem.getContent(), agendaItem.getVoteCount()))
                .collect(Collectors.toList());

        GradeType targetGradeType = agenda.getTarget();
        int totalMember = memberGradeRepository.countByGrade_GradeType(targetGradeType);
        int votedMember = voteRepository.countDistinctMember(agenda);
        boolean voted = Vote.didMemberVote(agenda.getVotes(), memberId);

        return AgendaResponse.from(agenda, votedMember, totalMember, selectList, voted);
    }

    @Transactional
    public void deleteAgenda(Long memberId, Long agendaId) {
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);

        voteRepository.deleteByAgendaId(agendaId);
        agendaItemRepository.deleteByAgendaId(agendaId);
        agendaRepository.delete(agenda);
    }

    @Transactional
    public void updateAgenda(Long memberId, Long agendaId, AgendaUpdateRequest request) {
        validateAgendaUpdateRequest(request);
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);

        agenda.update(request.getTitle(), request.getContent(), request.getClosedAt(), request.getTarget());
    }

    private void validateAgendaUpdateRequest(AgendaUpdateRequest request) {
        AgendaUpdateValidator validator = new AgendaUpdateValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Transactional
    public void updateAgendaItems(Long memberId, Long agendaId, AgendaItemUpdateRequest request) {
        validateAgendaItemUpdateRequest(request);
        Agenda agenda = findAgenda(agendaId);
        agenda.validateOwner(memberId);
        agenda.validateAlreadyVoteStarted();

        agendaItemRepository.deleteByAgendaId(agendaId);
        List<AgendaItem> agendaItems = getAgendaItems(request.getSelectList());
        agenda.updateAgendaItems(agendaItems);
    }

    private void validateAgendaItemUpdateRequest(AgendaItemUpdateRequest request) {
        AgendaItemUpdateValidator validator = new AgendaItemUpdateValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private Agenda findAgenda(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new AgendaNotFoundException(agendaId));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));
    }
}
