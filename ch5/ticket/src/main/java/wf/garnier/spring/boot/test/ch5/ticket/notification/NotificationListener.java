package wf.garnier.spring.boot.test.ch5.ticket.notification;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketStatusChangedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

	private final NotificationBroadcaster broadcaster;

	public NotificationListener(NotificationBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}

	@Async
	@EventListener
	public void onTicketCreated(TicketCreatedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "CREATED",
				"New %s-priority ticket: %s".formatted(ticket.getPriority(), ticket.getTitle()));
		broadcaster.send(message);
	}

	@Async
	@EventListener
	public void onTicketAssigned(TicketAssignedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "ASSIGNED",
				"Ticket '%s' assigned to %s".formatted(ticket.getTitle(), event.newAgent().getName()));
		broadcaster.send(message);
	}

	@Async
	@EventListener
	public void onTicketStatusChanged(TicketStatusChangedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "STATUS",
				"Ticket '%s' changed from %s to %s".formatted(ticket.getTitle(), event.oldStatus(), event.newStatus()));
		broadcaster.send(message);
	}

}
