package wf.garnier.spring.boot.test.ch5.ticket.sla;

import java.time.LocalDateTime;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SlaRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long ticketId;

	private long resolutionTimeMinutes;

	@Enumerated(EnumType.STRING)
	private TicketPriority priority;

	private LocalDateTime resolvedAt;

	protected SlaRecord() {
	}

	public SlaRecord(Long ticketId, long resolutionTimeMinutes, TicketPriority priority, LocalDateTime resolvedAt) {
		this.ticketId = ticketId;
		this.resolutionTimeMinutes = resolutionTimeMinutes;
		this.priority = priority;
		this.resolvedAt = resolvedAt;
	}

	public Long getId() {
		return id;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public long getResolutionTimeMinutes() {
		return resolutionTimeMinutes;
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

}
