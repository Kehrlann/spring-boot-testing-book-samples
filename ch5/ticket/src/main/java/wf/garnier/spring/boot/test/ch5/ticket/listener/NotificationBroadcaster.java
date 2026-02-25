package wf.garnier.spring.boot.test.ch5.ticket.listener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class NotificationBroadcaster {

	private static final int RECENT_BUFFER_SIZE = 2;

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	private final Deque<NotificationMessage> recentMessages = new ArrayDeque<>();

	public SseEmitter register() {
		var emitter = new SseEmitter(0L);
		emitters.add(emitter);
		emitter.onCompletion(() -> {
			System.out.println("🤡🤡🤡🤡 COMPLETION");
            emitters.remove(emitter);
        });
		emitter.onTimeout(() -> {
			System.out.println("🤡🤡🤡🤡 TIMEOUT");
            emitters.remove(emitter);
        });
		emitter.onError((_ex) -> {
			System.out.println("🤡🤡🤡🤡 ERROR");
            emitters.remove(emitter);
        });
		synchronized (recentMessages) {
			for (var message : recentMessages) {
				try {
					emitter.send(SseEmitter.event().name("ticket-notification").data(message));
				}
				catch (Exception ex) {
					emitters.remove(emitter);
					break;
				}
			}
		}
		return emitter;
	}

	@PreDestroy
	public void shutdown() {
		for (var emitter : emitters) {
			emitter.complete();
		}
		emitters.clear();
	}

	public void send(NotificationMessage message) {
		synchronized (recentMessages) {
			recentMessages.addFirst(message);
			while (recentMessages.size() > RECENT_BUFFER_SIZE) {
				recentMessages.removeLast();
			}
		}
		for (var emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("ticket-notification").data(message));
			}
			catch (Exception ex) {
				emitters.remove(emitter);
			}
		}
	}

}
