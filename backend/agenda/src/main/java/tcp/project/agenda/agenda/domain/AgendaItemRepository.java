package tcp.project.agenda.agenda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
}
