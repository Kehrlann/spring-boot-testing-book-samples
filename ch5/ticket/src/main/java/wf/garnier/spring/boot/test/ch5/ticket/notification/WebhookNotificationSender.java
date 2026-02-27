package wf.garnier.spring.boot.test.ch5.ticket.notification;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationSender implements NotificationSender {

	private static final Logger logger = LoggerFactory.getLogger(WebhookNotificationSender.class);

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.WEBHOOK;
	}

	@Override
	public void send(Agent agent, NotificationMessage message) {
		logger.info("Posting webhook for {}: {}", agent.getName(), message.title());
	}

}
