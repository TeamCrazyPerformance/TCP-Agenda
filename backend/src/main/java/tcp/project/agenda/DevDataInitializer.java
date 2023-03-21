package tcp.project.agenda;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tcp.project.agenda.member.domain.GradeRepository;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.MemberGrade;
import tcp.project.agenda.member.domain.MemberGradeRepository;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.MemberRepository;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements InitializingBean {

    private final MemberRepository memberRepository;
    private final MemberGradeRepository memberGradeRepository;
    private final GradeRepository gradeRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Grade executiveGrade = gradeRepository.save(new Grade(GradeType.EXECUTIVE));
        Grade regularGrade = gradeRepository.save(new Grade(GradeType.REGULAR));
        Grade generalGrade = gradeRepository.save(new Grade(GradeType.GENERAL));
        Member executive = memberRepository.save(new Member("김감투", "17000000", "김감투"));
        Member regular = memberRepository.save(new Member("이정회원", "18000000", "이정회원"));
        Member general = memberRepository.save(new Member("박회원", "19000000", "박회원"));

        memberGradeRepository.save(new MemberGrade(executive, executiveGrade));
        memberGradeRepository.save(new MemberGrade(regular, regularGrade));
        memberGradeRepository.save(new MemberGrade(general, generalGrade));
    }
}
