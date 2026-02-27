package wf.garnier.spring.boot.test.ch5.ticket.notification;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketAssignedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AgentNotifier {

	private final Map<NotificationChannel, NotificationSender> senders;

	private final NotificationSender defaultSender;

	public AgentNotifier(List<NotificationSender> senders) {
		this.senders = senders.stream().collect(Collectors.toMap(NotificationSender::channel, Function.identity()));
		this.defaultSender = this.senders.get(NotificationChannel.EMAIL);
	}

	@EventListener
	public void onTicketAssigned(TicketAssignedEvent event) {
		var agent = event.newAgent();
		var message = new NotificationMessage(event.ticket().getId(), event.ticket().getTitle(), "ASSIGNED",
				"Ticket assigned to you");
		this.senders.getOrDefault(agent.getNotificationChannel(), this.defaultSender).send(agent, message);
	}

}
