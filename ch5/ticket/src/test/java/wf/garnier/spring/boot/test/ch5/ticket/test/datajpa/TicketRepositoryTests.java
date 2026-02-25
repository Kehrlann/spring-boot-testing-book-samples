package wf.garnier.spring.boot.test.ch5.ticket.test.datajpa;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketRepository;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketRepositoryTests {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	TicketRepository ticketRepository;

	@Test
	void findOverdueTickets() {
		var oldTicket = new Ticket("Old bug", "Created a week ago", TicketPriority.HIGH);
		entityManager.persist(oldTicket);

		var recentTicket = new Ticket("New bug", "Just created", TicketPriority.LOW);
		entityManager.persist(recentTicket);

		entityManager.flush();

		var threshold = LocalDateTime.now().minusDays(1);
		var overdue = ticketRepository.findOverdueTickets(threshold);

		assertThat(overdue).isEmpty();
	}

	@Test
	void findOverdueTicketsExcludesResolved() {
		var resolvedTicket = new Ticket("Fixed bug", "Already resolved", TicketPriority.MEDIUM);
		resolvedTicket.setStatus(TicketStatus.RESOLVED);
		entityManager.persist(resolvedTicket);
		entityManager.flush();

		var threshold = LocalDateTime.now().plusDays(1);
		var overdue = ticketRepository.findOverdueTickets(threshold);

		assertThat(overdue).isEmpty();
	}

	@Test
	void findOverdueTicketsIncludesOldOpenTickets() {
		var oldTicket = new Ticket("Old bug", "Created long ago", TicketPriority.CRITICAL);
		entityManager.persist(oldTicket);
		entityManager.flush();

		var threshold = LocalDateTime.now().plusMinutes(1);
		var overdue = ticketRepository.findOverdueTickets(threshold);

		assertThat(overdue).hasSize(1).first().satisfies(t -> {
			assertThat(t.getTitle()).isEqualTo("Old bug");
			assertThat(t.getStatus()).isEqualTo(TicketStatus.OPEN);
		});
	}

	@Test
	void countByStatusAndPriority() {
		entityManager.persist(new Ticket("Bug 1", "First", TicketPriority.HIGH));
		entityManager.persist(new Ticket("Bug 2", "Second", TicketPriority.HIGH));
		entityManager.persist(new Ticket("Bug 3", "Third", TicketPriority.LOW));
		entityManager.flush();

		var count = ticketRepository.countByStatusAndPriority(TicketStatus.OPEN, TicketPriority.HIGH);

		assertThat(count).isEqualTo(2);
	}

	@Test
	void countByStatusAndPriorityReturnsZeroWhenNoMatch() {
		entityManager.persist(new Ticket("Bug 1", "First", TicketPriority.LOW));
		entityManager.flush();

		var count = ticketRepository.countByStatusAndPriority(TicketStatus.OPEN, TicketPriority.CRITICAL);

		assertThat(count).isEqualTo(0);
	}

	@Test
	void findByStatus() {
		entityManager.persist(new Ticket("Open bug", "Still open", TicketPriority.MEDIUM));
		var resolvedTicket = new Ticket("Resolved bug", "Done", TicketPriority.LOW);
		resolvedTicket.setStatus(TicketStatus.RESOLVED);
		entityManager.persist(resolvedTicket);
		entityManager.flush();

		var openTickets = ticketRepository.findByStatus(TicketStatus.OPEN);

		assertThat(openTickets).hasSize(1).first().satisfies(t -> assertThat(t.getTitle()).isEqualTo("Open bug"));
	}

	@Test
	void findByAssignedAgentId() {
		var agent = new Agent("Alice", "alice@example.com");
		entityManager.persist(agent);
		var ticket = new Ticket("Assigned ticket", "Assigned to Alice", TicketPriority.HIGH);
		ticket.setAssignedAgent(agent);
		entityManager.persist(ticket);
		entityManager.persist(new Ticket("Unassigned ticket", "No agent", TicketPriority.LOW));
		entityManager.flush();

		var tickets = ticketRepository.findByAssignedAgentId(agent.getId());

		assertThat(tickets).hasSize(1).first().satisfies(t -> assertThat(t.getTitle()).isEqualTo("Assigned ticket"));
	}

}
