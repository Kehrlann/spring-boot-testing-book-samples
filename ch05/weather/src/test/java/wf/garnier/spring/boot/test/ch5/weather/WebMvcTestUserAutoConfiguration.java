package wf.garnier.spring.boot.test.ch5.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;

/**
 * This configuration will be loaded in all {@link WebMvcTest}-based tests, because it is
 * referenced in the
 * {@code org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest.imports} file in
 * {@code META-INF/spring}.
 */
class WebMvcTestUserAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(WebMvcTestUserAutoConfiguration.class);

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			log.info("🧪🧪 Hello for user-provided test autoconfiguration. This log line comes from {}.",
					this.getClass().getSimpleName());
		};
	}

}
