package tcp.project.agenda;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tcp.project.agenda.agenda.domain.Agenda;
import tcp.project.agenda.agenda.domain.AgendaItem;
import tcp.project.agenda.agenda.domain.AgendaRepository;
import tcp.project.agenda.agenda.domain.Vote;
import tcp.project.agenda.agenda.domain.VoteRepository;
import tcp.project.agenda.member.domain.GradeRepository;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.MemberGrade;
import tcp.project.agenda.member.domain.MemberGradeRepository;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements InitializingBean {

    private final MemberRepository memberRepository;
    private final MemberGradeRepository memberGradeRepository;
    private final GradeRepository gradeRepository;
    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;


    @Override
    public void afterPropertiesSet() throws Exception {
        Grade executiveGrade = gradeRepository.save(new Grade(GradeType.EXECUTIVE));
        Grade regularGrade = gradeRepository.save(new Grade(GradeType.REGULAR));
        Grade generalGrade = gradeRepository.save(new Grade(GradeType.GENERAL));
        Member executive = memberRepository.save(new Member("김감투", "17000000", "김감투"));
        Member regular1 = memberRepository.save(new Member("이정회원", "18000000", "이정회원"));
        Member regular2 = memberRepository.save(new Member("이정회원2", "18000001", "이정회원2"));
        Member general = memberRepository.save(new Member("박회원", "19000000", "박회원"));

        memberGradeRepository.save(new MemberGrade(executive, executiveGrade));
        memberGradeRepository.save(new MemberGrade(regular1, regularGrade));
        memberGradeRepository.save(new MemberGrade(regular2, regularGrade));
        memberGradeRepository.save(new MemberGrade(general, generalGrade));

        AgendaItem item1_1 = AgendaItem.createAgendaItem("투표 항목1-1");
        AgendaItem item1_2 = AgendaItem.createAgendaItem("투표 항목1-2");
        Agenda agenda1 = Agenda.createAgendaFrom(regular2, "안건1", "안건 내용1", GradeType.REGULAR, LocalDateTime.now().plusDays(1));
        agenda1.updateAgendaItems(List.of(item1_1, item1_2));

        AgendaItem item2_1 = AgendaItem.createAgendaItem("투표 항목2-1");
        AgendaItem item2_2 = AgendaItem.createAgendaItem("투표 항목2-2");
        Agenda agenda2 = Agenda.createAgendaFrom(general, "안건2", "안건 내용2", GradeType.REGULAR, LocalDateTime.now().plusDays(4));
        agenda2.updateAgendaItems(List.of(item2_1, item2_2));
        agendaRepository.save(agenda1);
        agendaRepository.save(agenda2);

        voteRepository.save(Vote.createVote(regular2, item1_1, agenda1));
        voteRepository.save(Vote.createVote(general, item2_2, agenda2));
    }
}
