package wf.garnier.spring.boot.test.ch5.ticket.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	List<AuditLog> findByTicketId(Long ticketId);

}
