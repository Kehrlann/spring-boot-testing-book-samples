package wf.garnier.spring.boot.test.ch5.ticket.notification;

public record NotificationMessage(Long ticketId, String title, String eventType, String message) {
}
