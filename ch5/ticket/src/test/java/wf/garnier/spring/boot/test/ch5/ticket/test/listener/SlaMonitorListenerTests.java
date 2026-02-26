package wf.garnier.spring.boot.test.ch5.ticket.test.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.event.TicketResolvedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.sla.SlaRecordRepository;
import wf.garnier.spring.boot.test.ch5.ticket.test.slice.EventListenerTest;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@EventListenerTest
class SlaMonitorListenerTests {

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	SlaRecordRepository slaRecordRepository;

	@MockitoBean
	NotificationBroadcaster notificationBroadcaster;

	@BeforeEach
	void clearSlaRecords() {
		slaRecordRepository.deleteAll();
	}

	@Test
	void recordsSlaOnTicketResolved() {
		var ticket = new Ticket("Fixed bug", "Was broken", TicketPriority.HIGH);
		eventPublisher.publishEvent(new TicketResolvedEvent(ticket));

		var records = slaRecordRepository.findAll();
		assertThat(records).hasSize(1).first().satisfies(sla -> {
			assertThat(sla.getResolutionTimeMinutes()).isGreaterThanOrEqualTo(0);
			assertThat(sla.getResolvedAt()).isNotNull();
		});
	}

}
