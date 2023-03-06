package tcp.project.agenda.common.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import tcp.project.agenda.common.annotation.ApplicationTest;
import tcp.project.agenda.member.domain.Grade;
import tcp.project.agenda.member.domain.GradeRepository;
import tcp.project.agenda.member.domain.GradeType;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberGrade;
import tcp.project.agenda.member.domain.MemberGradeRepository;
import tcp.project.agenda.member.domain.MemberRepository;

import static tcp.project.agenda.common.fixture.MemberFixture.getExecutiveMember;
import static tcp.project.agenda.common.fixture.MemberFixture.getGeneralMember;
import static tcp.project.agenda.common.fixture.MemberFixture.getRegularMember;

@ApplicationTest
public class ApplicationServiceTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected GradeRepository gradeRepository;

    @Autowired
    protected MemberGradeRepository memberGradeRepository;

    protected Member executive;
    protected Member regular;
    protected Member general;

    protected Grade executiveGrade;
    protected Grade regularGrade;
    protected Grade generalGrade;

    @BeforeEach
    void init() {
        executiveGrade = gradeRepository.save(new Grade(GradeType.EXECUTIVE));
        regularGrade = gradeRepository.save(new Grade(GradeType.REGULAR));
        generalGrade = gradeRepository.save(new Grade(GradeType.GENERAL));
        executive = memberRepository.save(getExecutiveMember());
        regular = memberRepository.save(getRegularMember());
        general = memberRepository.save(getGeneralMember());

        memberGradeRepository.save(new MemberGrade(executive, executiveGrade));
        memberGradeRepository.save(new MemberGrade(regular, regularGrade));
        memberGradeRepository.save(new MemberGrade(general, generalGrade));
    }
}
