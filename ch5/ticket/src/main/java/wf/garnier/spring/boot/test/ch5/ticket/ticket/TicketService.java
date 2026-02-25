package wf.garnier.spring.boot.test.ch5.ticket.ticket;

import java.util.List;
import java.util.Optional;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;
import wf.garnier.spring.boot.test.ch5.ticket.agent.AgentRepository;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketAssignedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketCreatedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketResolvedEvent;
import wf.garnier.spring.boot.test.ch5.ticket.event.TicketStatusChangedEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

	private final TicketRepository ticketRepository;

	private final CommentRepository commentRepository;

	private final AgentRepository agentRepository;

	private final ApplicationEventPublisher eventPublisher;

	public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository,
			AgentRepository agentRepository, ApplicationEventPublisher eventPublisher) {
		this.ticketRepository = ticketRepository;
		this.commentRepository = commentRepository;
		this.agentRepository = agentRepository;
		this.eventPublisher = eventPublisher;
	}

	public List<Ticket> findAll() {
		return ticketRepository.findAll();
	}

	public List<Ticket> findByStatus(TicketStatus status) {
		return ticketRepository.findByStatus(status);
	}

	public Optional<Ticket> findById(Long id) {
		return ticketRepository.findById(id);
	}

	public List<Comment> findComments(Long ticketId) {
		return commentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
	}

	@Transactional
	public Ticket create(String title, String description, TicketPriority priority) {
		var ticket = new Ticket(title, description, priority);
		ticket = ticketRepository.save(ticket);
		eventPublisher.publishEvent(new TicketCreatedEvent(ticket));
		return ticket;
	}

	@Transactional
	public Comment addComment(Long ticketId, String authorName, String content) {
		var ticket = ticketRepository.findById(ticketId).orElseThrow();
		var comment = new Comment(ticket, authorName, content);
		return commentRepository.save(comment);
	}

	@Transactional
	public Ticket assign(Long ticketId, Long agentId) {
		var ticket = ticketRepository.findById(ticketId).orElseThrow();
		var agent = agentRepository.findById(agentId).orElseThrow();
		var previousAgent = ticket.getAssignedAgent();
		ticket.setAssignedAgent(agent);
		ticket.setStatus(TicketStatus.IN_PROGRESS);
		ticket = ticketRepository.save(ticket);
		eventPublisher.publishEvent(new TicketAssignedEvent(ticket, previousAgent, agent));
		return ticket;
	}

	@Transactional
	public Ticket resolve(Long ticketId) {
		var ticket = ticketRepository.findById(ticketId).orElseThrow();
		ticket.setStatus(TicketStatus.RESOLVED);
		ticket = ticketRepository.save(ticket);
		eventPublisher.publishEvent(new TicketResolvedEvent(ticket));
		return ticket;
	}

	@Transactional
	public Ticket updateStatus(Long ticketId, TicketStatus status) {
		var ticket = ticketRepository.findById(ticketId).orElseThrow();
		var oldStatus = ticket.getStatus();
		ticket.setStatus(status);
		ticket = ticketRepository.save(ticket);
		eventPublisher.publishEvent(new TicketStatusChangedEvent(ticket, oldStatus, status));
		if (status == TicketStatus.RESOLVED && oldStatus != TicketStatus.RESOLVED) {
			eventPublisher.publishEvent(new TicketResolvedEvent(ticket));
		}
		return ticket;
	}

	public List<Agent> findAllAgents() {
		return agentRepository.findAll();
	}

}
