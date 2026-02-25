package wf.garnier.spring.boot.test.ch5.ticket.test.datajpa;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Comment;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.CommentRepository;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTests {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	CommentRepository commentRepository;

	@Test
	void findByTicketIdOrderedByCreatedAtDesc() {
		var ticket = new Ticket("Some ticket", "Description", TicketPriority.MEDIUM);
		entityManager.persist(ticket);

		var first = new Comment(ticket, "Alice", "First comment");
		entityManager.persist(first);
		var second = new Comment(ticket, "Bob", "Second comment");
		entityManager.persist(second);
		entityManager.flush();

		var comments = commentRepository.findByTicketIdOrderByCreatedAtDesc(ticket.getId());

		assertThat(comments).hasSize(2);
		assertThat(comments.get(0).getAuthorName()).isEqualTo("Bob");
		assertThat(comments.get(1).getAuthorName()).isEqualTo("Alice");
	}

	@Test
	void findByTicketIdReturnsEmptyForUnknownTicket() {
		var comments = commentRepository.findByTicketIdOrderByCreatedAtDesc(999L);

		assertThat(comments).isEmpty();
	}

	@Test
	void findByTicketIdDoesNotReturnCommentsFromOtherTickets() {
		var ticket1 = new Ticket("Ticket 1", "First", TicketPriority.HIGH);
		entityManager.persist(ticket1);
		var ticket2 = new Ticket("Ticket 2", "Second", TicketPriority.LOW);
		entityManager.persist(ticket2);
		entityManager.persist(new Comment(ticket1, "Alice", "Comment on ticket 1"));
		entityManager.persist(new Comment(ticket2, "Bob", "Comment on ticket 2"));
		entityManager.flush();

		var comments = commentRepository.findByTicketIdOrderByCreatedAtDesc(ticket1.getId());

		assertThat(comments).hasSize(1)
			.first()
			.satisfies(c -> assertThat(c.getContent()).isEqualTo("Comment on ticket 1"));
	}

}
