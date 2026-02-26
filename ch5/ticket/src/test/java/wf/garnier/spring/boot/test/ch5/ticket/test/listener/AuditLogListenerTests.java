package wf.garnier.spring.boot.test.ch5.ticket.test.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketResolvedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.audit.AuditLogRepository;
import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.test.slice.EventListenerTest;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@EventListenerTest
class AuditLogListenerTests {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	AuditLogRepository auditLogRepository;

	@MockitoBean
	NotificationBroadcaster notificationBroadcaster;

	@BeforeEach
	void clearAuditLogs() {
		auditLogRepository.deleteAll();
	}

	@Test
	void logsTicketCreatedEvent() {
		var ticket = new Ticket("Login broken", "500 error", TicketPriority.CRITICAL);
		eventPublisher.publishEvent(new TicketCreatedEvent(ticket));

		var logs = auditLogRepository.findAll();
		assertThat(logs).hasSize(1).first().satisfies(log -> {
			assertThat(log.getEventType()).isEqualTo("CREATED");
			assertThat(log.getDetails()).contains("Login broken");
		});
	}

	@Test
	void logsTicketAssignedEvent() {
		var ticket = new Ticket("Bug", "Description", TicketPriority.HIGH);
		var agent = new Agent("Alice", "alice@example.com");
		eventPublisher.publishEvent(new TicketAssignedEvent(ticket, null, agent));

		var logs = auditLogRepository.findAll();
		assertThat(logs).hasSize(1).first().satisfies(log -> {
			assertThat(log.getEventType()).isEqualTo("ASSIGNED");
			assertThat(log.getDetails()).contains("unassigned").contains("Alice");
		});
	}

	@Test
	void logsTicketResolvedEvent() {
		var ticket = new Ticket("Typo", "Fixed", TicketPriority.LOW);
		eventPublisher.publishEvent(new TicketResolvedEvent(ticket));

		var logs = auditLogRepository.findAll();
		assertThat(logs).hasSize(1).first().satisfies(log -> {
			assertThat(log.getEventType()).isEqualTo("RESOLVED");
			assertThat(log.getDetails()).contains("Typo");
		});
	}

}
