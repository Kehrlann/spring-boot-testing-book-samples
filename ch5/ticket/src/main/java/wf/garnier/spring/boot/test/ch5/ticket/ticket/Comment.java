package wf.garnier.spring.boot.test.ch5.ticket.ticket;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Ticket ticket;

	private String authorName;

	private String content;

	private LocalDateTime createdAt;

	protected Comment() {
	}

	public Comment(Ticket ticket, String authorName, String content) {
		this.ticket = ticket;
		this.authorName = authorName;
		this.content = content;
		this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public Long getId() {
		return id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Comment comment = (Comment) o;
		return Objects.equals(id, comment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
