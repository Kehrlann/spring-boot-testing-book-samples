package wf.garnier.spring.boot.test.ch5.ticket.sla;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

import org.springframework.stereotype.Service;

@Service
public class SlaBreachChecker {

	private final SlaRecordRepository slaRecordRepository;

	private final SlaPolicyProvider policyProvider;

	public SlaBreachChecker(SlaRecordRepository slaRecordRepository, SlaPolicyProvider policyProvider) {
		this.slaRecordRepository = slaRecordRepository;
		this.policyProvider = policyProvider;
	}

	public boolean isBreached(Ticket ticket) {
		return slaRecordRepository.findByTicketId(ticket.getId())
			.map(record -> record.getResolutionTimeMinutes() > policyProvider.forPriority(ticket.getPriority())
				.maxResolutionMinutes())
			.orElse(false);
	}

}
