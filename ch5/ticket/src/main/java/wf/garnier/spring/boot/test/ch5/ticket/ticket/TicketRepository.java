package wf.garnier.spring.boot.test.ch5.ticket.ticket;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	List<Ticket> findByStatus(TicketStatus status);

	List<Ticket> findByAssignedAgentId(Long agentId);

	@Query("""
			SELECT t FROM Ticket t
			WHERE t.status IN (wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus.OPEN,
			                   wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus.IN_PROGRESS)
			AND t.createdAt < :threshold
			ORDER BY t.createdAt ASC
			""")
	List<Ticket> findOverdueTickets(@Param("threshold") LocalDateTime threshold);

	@Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.priority = :priority")
	long countByStatusAndPriority(@Param("status") TicketStatus status, @Param("priority") TicketPriority priority);

}
