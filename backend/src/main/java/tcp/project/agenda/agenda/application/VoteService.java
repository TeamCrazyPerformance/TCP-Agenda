package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.SelectedAgendaItemDto;
import tcp.project.agenda.agenda.application.dto.VoteRequest;
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
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.common.exception.ValidationError;
import tcp.project.agenda.common.exception.ValidationException;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void vote(Long memberId, Long agendaId, VoteRequest request) {
        validateVoteRequest(request);
        Agenda agenda = findAgenda(agendaId);
        Member member = findMember(memberId);

        List<Long> selectItemIdList = getSelectItemIdList(request);
        validateVote(agenda, member, selectItemIdList);

        List<AgendaItem> selectedAgendaItems = agenda.getAgendaItems().stream()
                .filter(agendaItem -> selectItemIdList.contains(agendaItem.getId()))
                .toList();

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

    private void validateVote(Agenda agenda, Member member, List<Long> selectItemIdList) {
        agenda.validateAlreadyClosed();
        agenda.validateIsTargetGrade(member.getGrades());
        validateAlreadyVote(member.getId(), agenda.getId());
        validateExistAgendaItem(agenda.getAgendaItems(), selectItemIdList);
    }

    private void validateAlreadyVote(Long memberId, Long agendaId) {
        if (voteRepository.existsByMemberIdAndAgendaId(memberId, agendaId)) {
            throw new AlreadyVoteException(agendaId);
        }
    }

    private void validateExistAgendaItem(List<AgendaItem> agendaItems, List<Long> selectItemIdList) {
        boolean isExistAgendaItem = agendaItems.stream()
                .anyMatch(agendaItem -> selectItemIdList.contains(agendaItem.getId()));
        if (!isExistAgendaItem) {
            throw new AgendaItemNotFoundException(0L);
        }
    }

    @Transactional
    public void cancelVote(Long memberId, Long agendaId) {
        List<Vote> votes = voteRepository.findByMemberIdAndAgendaId(memberId, agendaId);
        voteRepository.deleteAllInBatch(votes);
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
