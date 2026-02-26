package wf.garnier.spring.boot.test.ch5.ticket.ticket.event;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus;

public record TicketStatusChangedEvent(Ticket ticket, TicketStatus oldStatus,
		TicketStatus newStatus) implements TicketEvent {
}
