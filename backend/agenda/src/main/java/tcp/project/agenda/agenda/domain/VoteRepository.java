package tcp.project.agenda.agenda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByMember_IdAndAgenda_Id(Long memberId, Long agendaId);
}
