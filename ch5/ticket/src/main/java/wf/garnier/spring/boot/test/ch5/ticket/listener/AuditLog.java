package wf.garnier.spring.boot.test.ch5.ticket.listener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long ticketId;

	private String eventType;

	private String details;

	private LocalDateTime timestamp;

	protected AuditLog() {
	}

	public AuditLog(Long ticketId, String eventType, String details) {
		this.ticketId = ticketId;
		this.eventType = eventType;
		this.details = details;
		this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public Long getId() {
		return id;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public String getEventType() {
		return eventType;
	}

	public String getDetails() {
		return details;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}
