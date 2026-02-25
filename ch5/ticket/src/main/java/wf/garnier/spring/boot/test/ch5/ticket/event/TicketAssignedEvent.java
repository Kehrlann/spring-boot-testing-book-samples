package wf.garnier.spring.boot.test.ch5.ticket.event;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;

public record TicketAssignedEvent(Ticket ticket, Agent previousAgent, Agent newAgent) implements TicketEvent {
}
