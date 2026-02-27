package wf.garnier.spring.boot.test.ch5.ticket.test.listener;

import java.io.IOException;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.test.slice.EventListenerTest;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketCreatedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static org.assertj.core.api.Assertions.assertThat;

@EventListenerTest
class NotificationListenerTests {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	NotificationBroadcaster notificationBroadcaster;

	private RecordingSseEmitter emitter = new RecordingSseEmitter();

	static class RecordingSseEmitter extends SseEmitter {

		String emittedEvent;

		@Override
		public void send(SseEventBuilder builder) throws IOException {
			emittedEvent = builder.build()
				.stream()
				.map(DataWithMediaType::getData)
				.map(Object::toString)
				.collect(Collectors.joining());
		}

		public String getEmittedEvent() {
			return emittedEvent;
		}

	}

	@BeforeEach
	void setUp() {
		notificationBroadcaster.register(emitter);
	}

	@Test
	void broadcastsNotificationOnTicketCreated() throws IOException {
		var ticket = new Ticket("Server down", "Production outage", TicketPriority.CRITICAL);
		eventPublisher.publishEvent(new TicketCreatedEvent(ticket));

		Awaitility.await()
			.untilAsserted(() -> assertThat(emitter.getEmittedEvent()).contains("event:ticket-notification")
				.contains(
						"data:NotificationMessage[ticketId=null, title=Server down, eventType=CREATED, message=New CRITICAL-priority ticket: Server down]"));
	}

	@Test
	void broadcastsNotificationOnTicketAssigned() {
		var ticket = new Ticket("UI glitch", "Button misaligned", TicketPriority.LOW);
		var agent = new Agent("Bob", "bob@example.com");
		eventPublisher.publishEvent(new TicketAssignedEvent(ticket, null, agent));

		Awaitility.await()
			.untilAsserted(() -> assertThat(emitter.getEmittedEvent()).contains("event:ticket-notification")
				.contains(
						"data:NotificationMessage[ticketId=null, title=UI glitch, eventType=ASSIGNED, message=Ticket 'UI glitch' assigned to Bob]"));
	}

}
