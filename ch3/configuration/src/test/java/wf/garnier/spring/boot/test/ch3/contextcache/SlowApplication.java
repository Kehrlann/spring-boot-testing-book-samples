package wf.garnier.spring.boot.test.ch3.contextcache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * This test app lives outside the main
 * {@code wf.garnier.spring.boot.test.ch3.configuration} package, so it's not picked up by
 * the component scan of the main app.
 */
// tag::code[]
@SpringBootApplication
class SlowApplication {

	//@formatter:off
	@Bean
	SlowBean slowBean(
			@Value("${startup.delay:3s}") Duration startupDelay  // <1>
	) throws InterruptedException {
	//@formatter:on
		System.out.println(">>> Configuring slow bean...");
		Thread.sleep(startupDelay);
		return new SlowBean();
		// tag::ignored[]
	}

	@Bean
	FastBean fastBean() { // <2>
		return new FastBean();
	}

	record SlowBean() {
	}

	record FastBean() {
		// end::ignored[]
	}

}
// end::code[]