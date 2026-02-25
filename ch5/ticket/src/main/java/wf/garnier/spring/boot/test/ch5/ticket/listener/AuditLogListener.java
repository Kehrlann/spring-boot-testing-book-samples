package wf.garnier.spring.boot.test.ch5.ticket.listener;

import wf.garnier.spring.boot.test.ch5.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketResolvedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogListener {

	private final AuditLogRepository auditLogRepository;

	public AuditLogListener(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	@EventListener
	public void onTicketCreated(TicketCreatedEvent event) {
		var ticket = event.ticket();
		var details = "Ticket created: '%s' [%s]".formatted(ticket.getTitle(), ticket.getPriority());
		auditLogRepository.save(new AuditLog(ticket.getId(), "CREATED", details));
	}

	@EventListener
	public void onTicketAssigned(TicketAssignedEvent event) {
		var ticket = event.ticket();
		var previousName = event.previousAgent() != null ? event.previousAgent().getName() : "unassigned";
		var details = "Ticket reassigned from %s to %s".formatted(previousName, event.newAgent().getName());
		auditLogRepository.save(new AuditLog(ticket.getId(), "ASSIGNED", details));
	}

	@EventListener
	public void onTicketResolved(TicketResolvedEvent event) {
		var ticket = event.ticket();
		var details = "Ticket resolved: '%s'".formatted(ticket.getTitle());
		auditLogRepository.save(new AuditLog(ticket.getId(), "RESOLVED", details));
	}

}
