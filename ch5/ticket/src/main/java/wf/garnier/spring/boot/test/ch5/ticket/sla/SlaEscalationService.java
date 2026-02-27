package wf.garnier.spring.boot.test.ch5.ticket.sla;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketResolvedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SlaEscalationService {

	private static final Logger logger = LoggerFactory.getLogger(SlaEscalationService.class);

	private final SlaBreachChecker breachChecker;

	public SlaEscalationService(SlaBreachChecker breachChecker) {
		this.breachChecker = breachChecker;
	}

	@EventListener
	public void onTicketResolved(TicketResolvedEvent event) {
		if (breachChecker.isBreached(event.ticket())) {
			logger.warn("SLA BREACHED for ticket '{}' - escalating", event.ticket().getTitle());
		}
	}

}
