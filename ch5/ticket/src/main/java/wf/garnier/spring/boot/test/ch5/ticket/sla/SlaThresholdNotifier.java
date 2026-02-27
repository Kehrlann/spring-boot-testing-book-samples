package wf.garnier.spring.boot.test.ch5.ticket.sla;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SlaThresholdNotifier {

	private static final Logger logger = LoggerFactory.getLogger(SlaThresholdNotifier.class);

	private final SlaPolicyProvider policyProvider;

	public SlaThresholdNotifier(SlaPolicyProvider policyProvider) {
		this.policyProvider = policyProvider;
	}

	public void checkApproachingDeadlines(List<Ticket> openTickets) {
		for (var ticket : openTickets) {
			var policy = policyProvider.forPriority(ticket.getPriority());
			var elapsed = Duration.between(ticket.getCreatedAt(), LocalDateTime.now()).toMinutes();
			if (elapsed > policy.maxResolutionMinutes() * 0.8) {
				logger.warn("Ticket '{}' approaching SLA deadline ({}/{}min)", ticket.getTitle(), elapsed,
						policy.maxResolutionMinutes());
			}
		}
	}

}
