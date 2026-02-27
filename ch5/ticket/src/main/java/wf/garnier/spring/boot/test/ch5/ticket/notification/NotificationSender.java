package wf.garnier.spring.boot.test.ch5.ticket.notification;

import wf.garnier.spring.boot.test.ch5.ticket.agent.Agent;

public interface NotificationSender {

	NotificationChannel channel();

	void send(Agent agent, NotificationMessage message);

}
