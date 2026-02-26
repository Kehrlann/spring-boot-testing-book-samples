package wf.garnier.spring.boot.test.ch5.ticket.ticket.event;

import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

public record TicketCreatedEvent(Ticket ticket) implements TicketEvent {
}
