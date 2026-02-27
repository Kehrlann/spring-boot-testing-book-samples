package wf.garnier.spring.boot.test.ch5.ticket.notification;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.annotation.PreDestroy;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketStatusChangedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class NotificationBroadcaster {

	private static final int RECENT_BUFFER_SIZE = 2;

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	private final Deque<NotificationMessage> recentMessages = new ArrayDeque<>();

	public void register(SseEmitter emitter) {
		emitters.add(emitter);
		emitter.onCompletion(() -> {
			emitters.remove(emitter);
		});
		emitter.onTimeout(() -> {
			emitters.remove(emitter);
		});
		emitter.onError((_ex) -> {
			emitters.remove(emitter);
		});
		synchronized (recentMessages) {
			for (var message : recentMessages) {
				try {
					emitter.send(SseEmitter.event().name("ticket-notification").data(message));
				}
				catch (Exception ex) {
					emitters.remove(emitter);
					break;
				}
			}
		}
	}

	@PreDestroy
	public void shutdown() {
		for (var emitter : emitters) {
			emitter.complete();
		}
		emitters.clear();
	}

	@Async
	@EventListener
	public void onTicketCreated(TicketCreatedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "CREATED",
				"New %s-priority ticket: %s".formatted(ticket.getPriority(), ticket.getTitle()));
		this.send(message);
	}

	@Async
	@EventListener
	public void onTicketAssigned(TicketAssignedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "ASSIGNED",
				"Ticket '%s' assigned to %s".formatted(ticket.getTitle(), event.newAgent().getName()));
		this.send(message);
	}

	@Async
	@EventListener
	public void onTicketStatusChanged(TicketStatusChangedEvent event) {
		var ticket = event.ticket();
		var message = new NotificationMessage(ticket.getId(), ticket.getTitle(), "STATUS",
				"Ticket '%s' changed from %s to %s".formatted(ticket.getTitle(), event.oldStatus(), event.newStatus()));
		this.send(message);
	}

	public void send(NotificationMessage message) {
		synchronized (recentMessages) {
			recentMessages.addFirst(message);
			while (recentMessages.size() > RECENT_BUFFER_SIZE) {
				recentMessages.removeLast();
			}
		}
		for (var emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("ticket-notification").data(message));
			}
			catch (Exception ex) {
				emitters.remove(emitter);
			}
		}
	}

}
