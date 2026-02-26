package wf.garnier.spring.boot.test.ch5.ticket.sla;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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

	private LocalDateTime resolvedAt;

	protected SlaRecord() {
	}

	public SlaRecord(Long ticketId, long resolutionTimeMinutes, LocalDateTime resolvedAt) {
		this.ticketId = ticketId;
		this.resolutionTimeMinutes = resolutionTimeMinutes;
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

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

}
