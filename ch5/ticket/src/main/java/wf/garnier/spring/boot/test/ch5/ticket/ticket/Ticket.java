package wf.garnier.spring.boot.test.ch5.ticket.ticket;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String description;

	@Enumerated(EnumType.STRING)
	private TicketStatus status;

	@Enumerated(EnumType.STRING)
	private TicketPriority priority;

	@ManyToOne
	private Agent assignedAgent;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	protected Ticket() {
	}

	public Ticket(String title, String description, TicketPriority priority) {
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.status = TicketStatus.OPEN;
		this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		this.updatedAt = this.createdAt;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
		this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public Agent getAssignedAgent() {
		return assignedAgent;
	}

	public void setAssignedAgent(Agent assignedAgent) {
		this.assignedAgent = assignedAgent;
		this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Ticket ticket = (Ticket) o;
		return Objects.equals(id, ticket.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Ticket{id=" + id + ", title='" + title + "', status=" + status + ", priority=" + priority + "}";
	}

}
