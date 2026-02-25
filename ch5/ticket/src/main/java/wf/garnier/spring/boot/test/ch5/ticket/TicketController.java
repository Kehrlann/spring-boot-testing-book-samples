package wf.garnier.spring.boot.test.ch5.ticket;

import java.util.List;

import wf.garnier.spring.boot.test.ch5.ticket.listener.NotificationBroadcaster;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Comment;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.Ticket;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketPriority;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketService;
import wf.garnier.spring.boot.test.ch5.ticket.ticket.TicketStatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Controller
public class TicketController {

	private final TicketService ticketService;

	private final NotificationBroadcaster notificationBroadcaster;

	public TicketController(TicketService ticketService, NotificationBroadcaster notificationBroadcaster) {
		this.ticketService = ticketService;
		this.notificationBroadcaster = notificationBroadcaster;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("tickets", ticketService.findAll());
		return "index";
	}

	@GetMapping("/tickets/{id}")
	public String ticketDetail(@PathVariable Long id, Model model) {
		var ticket = ticketService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
		model.addAttribute("ticket", ticket);
		model.addAttribute("agents", ticketService.findAllAgents());
		model.addAttribute("statuses", TicketStatus.values());
		return "detail";
	}

	@PostMapping("/tickets/{id}/assign")
	public String assignTicketFromForm(@PathVariable Long id, @RequestParam Long agentId) {
		ticketService.assign(id, agentId);
		return "redirect:/tickets/" + id;
	}

	@PostMapping("/tickets/{id}/status")
	public String updateTicketStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
		ticketService.updateStatus(id, status);
		return "redirect:/tickets/" + id;
	}

	@GetMapping("/tickets/new")
	public String newTicketForm(Model model) {
		model.addAttribute("ticket", new CreateTicketForm());
		model.addAttribute("priorities", TicketPriority.values());
		return "create";
	}

	@PostMapping("/tickets")
	public String createTicketFromForm(@Valid CreateTicketForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("ticket", form);
			model.addAttribute("priorities", TicketPriority.values());
			return "create";
		}
		ticketService.create(form.getTitle(), form.getDescription(), form.getPriority());
		return "redirect:/";
	}

	@GetMapping("/api/tickets")
	@ResponseBody
	public List<Ticket> listTickets(@RequestParam(required = false) TicketStatus status) {
		if (status != null) {
			return ticketService.findByStatus(status);
		}
		return ticketService.findAll();
	}

	@PostMapping("/api/tickets")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Ticket createTicket(@Valid @RequestBody CreateTicketRequest request) {
		return ticketService.create(request.title(), request.description(), request.priority());
	}

	@GetMapping("/api/tickets/{id}")
	@ResponseBody
	public TicketDetail getTicket(@PathVariable Long id) {
		var ticket = ticketService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
		var comments = ticketService.findComments(id);
		return new TicketDetail(ticket, comments);
	}

	@PostMapping("/api/tickets/{id}/comments")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Comment addComment(@PathVariable Long id, @Valid @RequestBody AddCommentRequest request) {
		return ticketService.addComment(id, request.authorName(), request.content());
	}

	@PutMapping("/api/tickets/{id}/assign")
	@ResponseBody
	public Ticket assignTicket(@PathVariable Long id, @RequestBody AssignRequest request) {
		return ticketService.assign(id, request.agentId());
	}

	@PutMapping("/api/tickets/{id}/resolve")
	@ResponseBody
	public Ticket resolveTicket(@PathVariable Long id) {
		return ticketService.resolve(id);
	}

	@GetMapping(value = "/api/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamNotifications() {
		return notificationBroadcaster.register();
	}

	public record CreateTicketRequest(@NotBlank String title, String description, @NotNull TicketPriority priority) {
	}

	public record AddCommentRequest(@NotBlank String authorName, @NotBlank String content) {
	}

	public record AssignRequest(long agentId) {
	}

	public record TicketDetail(Ticket ticket, List<Comment> comments) {
	}

	public static class CreateTicketForm {

		@NotBlank
		private String title;

		private String description;

		@NotNull
		private TicketPriority priority;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public TicketPriority getPriority() {
			return priority;
		}

		public void setPriority(TicketPriority priority) {
			this.priority = priority;
		}

	}

}
