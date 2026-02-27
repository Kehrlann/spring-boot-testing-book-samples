package wf.garnier.spring.boot.test.ch5.ticket.notification;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

	private static final Logger logger = LoggerFactory.getLogger(SmsNotificationSender.class);

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}

	@Override
	public void send(Agent agent, NotificationMessage message) {
		logger.info("Sending SMS to {}: {}", agent.getName(), message.title());
	}

}
