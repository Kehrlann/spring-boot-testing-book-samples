package wf.garnier.spring.boot.test.ch5.ticket.sla;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;

public record SlaPolicy(TicketPriority priority, long maxResolutionMinutes) {
}
