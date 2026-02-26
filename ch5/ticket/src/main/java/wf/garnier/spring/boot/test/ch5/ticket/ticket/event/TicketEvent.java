package wf.garnier.spring.boot.test.ch5.ticket.ticket.event;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

public sealed interface TicketEvent
		permits TicketCreatedEvent, TicketAssignedEvent, TicketResolvedEvent, TicketStatusChangedEvent {

	Ticket ticket();

}
