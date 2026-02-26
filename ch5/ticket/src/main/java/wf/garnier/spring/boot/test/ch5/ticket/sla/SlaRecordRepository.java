package wf.garnier.spring.boot.test.ch5.ticket.sla;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaRecordRepository extends JpaRepository<SlaRecord, Long> {

	Optional<SlaRecord> findByTicketId(Long ticketId);

}
