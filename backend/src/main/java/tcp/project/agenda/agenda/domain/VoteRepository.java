package tcp.project.agenda.agenda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByMemberIdAndAgendaId(Long memberId, Long agendaId);

    @Query("select count(distinct v.member) from Vote v where v.agenda = :agenda")
    int countDistinctMember(@Param("agenda") Agenda agenda);

    boolean existsByMemberIdAndAgendaId(Long memberId, Long agendaId);

    @Modifying
    @Query("delete from Vote v where v.agenda.id = :agendaId")
    void deleteByAgendaId(@Param("agendaId") Long agendaId);
}
