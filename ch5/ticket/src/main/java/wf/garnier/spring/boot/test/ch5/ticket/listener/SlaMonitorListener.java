package wf.garnier.spring.boot.test.ch5.ticket.listener;

import java.time.Duration;
import java.time.LocalDateTime;

import wf.garnier.spring.boot.test.ch5.ticket.event.TicketResolvedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SlaMonitorListener {

	private final SlaRecordRepository slaRecordRepository;

	public SlaMonitorListener(SlaRecordRepository slaRecordRepository) {
		this.slaRecordRepository = slaRecordRepository;
	}

	@EventListener
	public void onTicketResolved(TicketResolvedEvent event) {
		var ticket = event.ticket();
		var resolutionTime = Duration.between(ticket.getCreatedAt(), LocalDateTime.now()).toMinutes();
		slaRecordRepository.save(new SlaRecord(ticket.getId(), resolutionTime, LocalDateTime.now()));
	}

}
