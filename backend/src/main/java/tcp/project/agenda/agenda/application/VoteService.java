package tcp.project.agenda.agenda.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.agenda.application.dto.SelectedAgendaItemDto;
import tcp.project.agenda.agenda.application.dto.VoteRequest;
import tcp.project.agenda.agenda.application.validator.VoteValidator;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
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
    private final MemberRepository memberRepository;


    @Transactional
    public void vote(Long memberId, Long agendaId, VoteRequest request) {
        validateVoteRequest(request);
        validateAlreadyVote(memberId, agendaId);
        Agenda agenda = findAgenda(agendaId);
        Member member = findMember(memberId);

        List<Long> selectItemIdList = getSelectItemIdList(request);

        List<Vote> votes = agenda.vote(member, selectItemIdList);
        voteRepository.saveAll(votes);
    }

    private void validateVoteRequest(VoteRequest request) {
        VoteValidator validator = new VoteValidator();
        List<ValidationError> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateAlreadyVote(Long memberId, Long agendaId) {
        if (voteRepository.existsByMemberIdAndAgendaId(memberId, agendaId)) {
            throw new AlreadyVoteException(agendaId);
        }
    }

    private List<Long> getSelectItemIdList(VoteRequest request) {
        return request.getSelectList().stream()
                .map(SelectedAgendaItemDto::getId)
                .collect(Collectors.toList());
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
