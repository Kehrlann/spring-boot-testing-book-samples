package wf.garnier.spring.boot.test.ch5.ticket.test.listener;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationMessage;
import wf.garnier.spring.boot.test.ch5.ticket.test.slice.EventListenerTest;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@EventListenerTest
class NotificationListenerTests {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@MockitoBean
	NotificationBroadcaster notificationBroadcaster;

	@Test
	void broadcastsNotificationOnTicketCreated() {
		var ticket = new Ticket("Server down", "Production outage", TicketPriority.CRITICAL);
		eventPublisher.publishEvent(new TicketCreatedEvent(ticket));

		verify(notificationBroadcaster).send(argThat((NotificationMessage msg) -> msg.eventType().equals("CREATED")
				&& msg.title().equals("Server down") && msg.message().contains("CRITICAL")));
	}

	@Test
	void broadcastsNotificationOnTicketAssigned() {
		var ticket = new Ticket("UI glitch", "Button misaligned", TicketPriority.LOW);
		var agent = new Agent("Bob", "bob@example.com");
		eventPublisher.publishEvent(new TicketAssignedEvent(ticket, null, agent));

		verify(notificationBroadcaster).send(argThat((NotificationMessage msg) -> msg.eventType().equals("ASSIGNED")
				&& msg.message().contains("Bob") && msg.title().equals("UI glitch")));
	}

}
