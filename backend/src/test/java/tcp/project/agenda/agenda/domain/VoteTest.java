package tcp.project.agenda.agenda.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tcp.project.agenda.member.domain.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VoteTest {

    @Mock
    Agenda agenda;

    @Mock
    Member member;

    @Mock
    AgendaItem agendaItem;

    @Test
    @DisplayName("투표 리스트 중 투표를 한 멤버라면 true를 반환")
    void didMemberVoteTest_true() throws Exception {
        //given
        Vote vote = Vote.createVote(member, agendaItem, agenda);
        given(member.getId()).willReturn(1L);

        //when
        boolean res = Vote.didMemberVote(List.of(vote), 1L);

        //then
        assertThat(res).isTrue();
    }

    @Test
    @DisplayName("투표 리스트 중 투표를 하지 않은 멤버라면 false를 반환")
    void didMemberVoteTest_false() throws Exception {
        //given
        Vote vote = Vote.createVote(member, agendaItem, agenda);
        given(member.getId()).willReturn(1L);

        //when
        boolean res = Vote.didMemberVote(List.of(vote), 2L);

        //then
        assertThat(res).isFalse();
    }
}