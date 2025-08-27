package wf.garnier.spring.boot.test.ch4.weather.infrastructure;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
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

	private final Deque<Access> accessRecords = new ConcurrentLinkedDeque<>();

	private final AtomicInteger size = new AtomicInteger(0);

	private static final int MAX_SIZE = 50;

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		var record = new Access(request.getMethod(), request.getRequestURI());
		if (size.incrementAndGet() > MAX_SIZE) {
			accessRecords.removeFirst();
		}
		accessRecords.add(record);
		System.out.println("~~~~~~~~~~~~~ LOGGING REQUEST : " + record);
		getNext().invoke(request, response);
	}

	public Collection<Access> getAccessRecords() {
		return Collections.unmodifiableCollection(this.accessRecords);
	}

	synchronized public void clear() {
		this.accessRecords.clear();
		this.size.set(0);
	}

}
