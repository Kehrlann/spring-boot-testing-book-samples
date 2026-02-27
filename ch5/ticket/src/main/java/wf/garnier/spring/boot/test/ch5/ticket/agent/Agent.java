package wf.garnier.spring.boot.test.ch5.ticket.agent;

import java.util.Objects;

import wf.garnier.spring.boot.test.ch5.ticket.notification.NotificationChannel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Agent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	@Enumerated(EnumType.STRING)
	private NotificationChannel notificationChannel;

	protected Agent() {
	}

	public Agent(String name, String email) {
		this(name, email, NotificationChannel.EMAIL);
	}

	public Agent(String name, String email, NotificationChannel notificationChannel) {
		this.name = name;
		this.email = email;
		this.notificationChannel = notificationChannel;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public NotificationChannel getNotificationChannel() {
		return notificationChannel;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Agent agent = (Agent) o;
		return Objects.equals(id, agent.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Agent{id=" + id + ", name='" + name + "', email='" + email + "'}";
	}

}
