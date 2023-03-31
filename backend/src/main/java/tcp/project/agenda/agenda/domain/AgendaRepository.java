package tcp.project.agenda.agenda.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("select a from Agenda a join fetch a.member")
    Slice<Agenda> findSliceBy(Pageable pageable);
}
