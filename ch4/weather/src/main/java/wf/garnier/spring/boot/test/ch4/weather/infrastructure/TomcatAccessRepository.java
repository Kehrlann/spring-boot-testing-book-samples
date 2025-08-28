package wf.garnier.spring.boot.test.ch4.weather.infrastructure;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import org.springframework.stereotype.Component;

/**
 * Showcases some Servlet container-only business logic, that does not show up in MockMvc
 * tests.
 * <p>
 * Do NOT do this in production. If you'd like to record access, see
 * {@link org.apache.catalina.valves.AccessLogValve}.
 */
@Component
public class TomcatAccessRepository extends ValveBase {

	public record Access(String method, String path) {
	}

	private final RingBuffer<Access> accessRecords = new RingBuffer<>(50);

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		var record = new Access(request.getMethod(), request.getRequestURI());
		accessRecords.add(record);
		System.out.println("~~~~~~~~~~~~~ LOGGING REQUEST : " + record);
		getNext().invoke(request, response);
	}

	public Collection<Access> getAccessRecords() {
		return this.accessRecords.getEntries();
	}

	private static class RingBuffer<T> {

		private final Deque<T> entries = new ConcurrentLinkedDeque<>();

		private int size = 0;

		private final int maxSize;

		private RingBuffer(int maxSize) {
			this.maxSize = maxSize;
		}

		public synchronized void add(T entry) {
			if (this.size++ >= this.maxSize) {
				entries.removeFirst();
			}
			entries.add(entry);
		}

		public Collection<T> getEntries() {
			return Collections.unmodifiableCollection(this.entries);
		}

	}

}
