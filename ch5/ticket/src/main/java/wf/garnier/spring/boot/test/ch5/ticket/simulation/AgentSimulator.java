package wf.garnier.spring.boot.test.ch5.ticket.simulation;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import wf.garnier.spring.boot.test.ch5.ticket.agent.AgentRepository;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketService;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("simulation")
public class AgentSimulator {

	private static final Logger logger = LoggerFactory.getLogger(AgentSimulator.class);

	private final TicketService ticketService;

	private final AgentRepository agentRepository;

	public AgentSimulator(TicketService ticketService, AgentRepository agentRepository) {
		this.ticketService = ticketService;
		this.agentRepository = agentRepository;
	}

	@Scheduled(fixedRate = 30_000, initialDelay = 10_000)
	public void simulateActivity() {
		var random = ThreadLocalRandom.current();
		if (random.nextBoolean()) {
			reassignRandomTicket();
		}
		else {
			advanceRandomTicketStatus();
		}
	}

	private void reassignRandomTicket() {
		var tickets = ticketService.findAll();
		var agents = agentRepository.findAll();
		if (tickets.isEmpty() || agents.isEmpty()) {
			return;
		}

		var ticket = pickRandom(tickets);
		var agent = pickRandom(agents);
		ticketService.assign(ticket.getId(), agent.getId());
		logger.info("Simulation: reassigned ticket #{} '{}' to {}", ticket.getId(), ticket.getTitle(), agent.getName());
	}

	private void advanceRandomTicketStatus() {
		var tickets = ticketService.findAll().stream().filter(t -> t.getStatus() != TicketStatus.CLOSED).toList();
		if (tickets.isEmpty()) {
			return;
		}

		var ticket = pickRandom(tickets);
		var nextStatus = nextStatus(ticket.getStatus());
		ticketService.updateStatus(ticket.getId(), nextStatus);
		logger.info("Simulation: advanced ticket #{} '{}' from {} to {}", ticket.getId(), ticket.getTitle(),
				ticket.getStatus(), nextStatus);
	}

	private static TicketStatus nextStatus(TicketStatus current) {
		return switch (current) {
			case OPEN -> TicketStatus.IN_PROGRESS;
			case IN_PROGRESS -> TicketStatus.RESOLVED;
			case RESOLVED, CLOSED -> TicketStatus.CLOSED;
		};
	}

	private static <T> T pickRandom(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

}
