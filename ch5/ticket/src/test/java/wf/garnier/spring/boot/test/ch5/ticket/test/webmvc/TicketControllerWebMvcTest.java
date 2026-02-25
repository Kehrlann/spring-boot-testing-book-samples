package wf.garnier.spring.boot.test.ch5.ticket.test.webmvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.TicketController;
import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.listener.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Comment;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(TicketController.class)
class TicketControllerWebMvcTest {

	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	TicketService ticketService;

	@MockitoBean
	NotificationBroadcaster notificationBroadcaster;

	@Test
	void listTicketsReturnsJson() {
		var ticket = new Ticket("Login broken", "500 error on login", TicketPriority.CRITICAL);
		when(ticketService.findAll()).thenReturn(List.of(ticket));

		//@formatter:off
		mvc.get()
			.uri("/api/tickets")
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[0].title")
			.isEqualTo("Login broken");
		//@formatter:on
	}

	@Test
	void createTicketWithValidBody() {
		var ticket = new Ticket("New bug", "Something is wrong", TicketPriority.HIGH);
		when(ticketService.create(anyString(), anyString(), any())).thenReturn(ticket);

		//@formatter:off
		mvc.post()
			.uri("/api/tickets")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"title": "New bug",
					"description": "Something is wrong",
					"priority": "HIGH"
				}
				""")
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.CREATED)
			.bodyJson()
			.extractingPath("$.title")
			.isEqualTo("New bug");
		//@formatter:on
	}

	@Test
	void createTicketWithMissingTitleReturnsBadRequest() {
		//@formatter:off
		mvc.post()
			.uri("/api/tickets")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"description": "No title provided",
					"priority": "LOW"
				}
				""")
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.BAD_REQUEST);
		//@formatter:on
	}

	@Test
	void createTicketWithMissingPriorityReturnsBadRequest() {
		//@formatter:off
		mvc.post()
			.uri("/api/tickets")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"title": "Some bug",
					"description": "Missing priority"
				}
				""")
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.BAD_REQUEST);
		//@formatter:on
	}

	@Test
	void getTicketDetailReturnsTicketAndComments() {
		var ticket = new Ticket("Dashboard slow", "Takes 10 seconds", TicketPriority.HIGH);
		var comment = new Comment(ticket, "Alice", "Investigating.");
		when(ticketService.findById(1L)).thenReturn(Optional.of(ticket));
		when(ticketService.findComments(1L)).thenReturn(List.of(comment));

		//@formatter:off
		var response = mvc.get()
			.uri("/api/tickets/{id}", 1L)
			.exchange();

		assertThat(response)
			.hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$.ticket.title")
			.isEqualTo("Dashboard slow");

		assertThat(response)
			.bodyJson()
			.extractingPath("$.comments[0].authorName")
			.isEqualTo("Alice");
		//@formatter:on
	}

	@Test
	void getTicketNotFoundReturns404() {
		when(ticketService.findById(999L)).thenReturn(Optional.empty());

		//@formatter:off
		mvc.get()
			.uri("/api/tickets/{id}", 999L)
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.NOT_FOUND);
		//@formatter:on
	}

	@Test
	void assignTicketReturnsUpdatedTicket() {
		var ticket = new Ticket("Export broken", "Empty CSV", TicketPriority.MEDIUM);
		var agent = new Agent("Alice", "alice@example.com");
		ticket.setAssignedAgent(agent);
		when(ticketService.assign(eq(1L), eq(2L))).thenReturn(ticket);

		//@formatter:off
		mvc.put()
			.uri("/api/tickets/{id}/assign", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{ "agentId": 2 }
				""")
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$.assignedAgent.name")
			.isEqualTo("Alice");
		//@formatter:on
	}

	@Test
	void resolveTicketReturnsUpdatedTicket() {
		var ticket = new Ticket("Typo in email", "Welcom", TicketPriority.LOW);
		when(ticketService.resolve(1L)).thenReturn(ticket);

		//@formatter:off
		mvc.put()
			.uri("/api/tickets/{id}/resolve", 1L)
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.OK);
		//@formatter:on
	}

}
