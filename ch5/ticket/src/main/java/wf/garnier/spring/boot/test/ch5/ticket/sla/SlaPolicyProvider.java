package wf.garnier.spring.boot.test.ch5.ticket.sla;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import org.springframework.stereotype.Component;

@Component
public class SlaPolicyProvider {

	public SlaPolicy forPriority(TicketPriority priority) {
		return switch (priority) {
			case CRITICAL -> new SlaPolicy(priority, 60);
			case HIGH -> new SlaPolicy(priority, 240);
			case MEDIUM -> new SlaPolicy(priority, 480);
			case LOW -> new SlaPolicy(priority, 1440);
		};
	}

}
