package tcp.project.agenda.agenda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
    List<AgendaItem> findByIdIn(List<Long> idList);

    @Modifying(flushAutomatically = true)
    @Query("delete from AgendaItem ai where ai.agenda.id = :agendaId")
    void deleteByAgendaId(@Param("agendaId") Long agendaId);
}
