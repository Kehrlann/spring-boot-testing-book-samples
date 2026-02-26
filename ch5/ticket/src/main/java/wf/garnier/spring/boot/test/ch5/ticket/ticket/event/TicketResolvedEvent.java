package wf.garnier.spring.boot.test.ch5.ticket.ticket.event;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

public record TicketResolvedEvent(Ticket ticket) implements TicketEvent {
}
