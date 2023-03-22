package tcp.project.agenda.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberGradeRepository extends JpaRepository<MemberGrade, Long> {
    int countByGrade_GradeType(GradeType gradeType);
}
