package wf.garnier.spring.boot.test.ch7.weather;

import java.io.IOException;

import org.jspecify.annotations.NonNull;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * Trick class to detect whether docker is running on the local machine.
 */
@Component
public class DockerPresentEnvironmentPostProcessor
		implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEvent> {

	// Logging is not available during env post-processing, so we replay logs when the
	// logging system is online.
	private static final DeferredLog log = new DeferredLog();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		// Spring Boot's test framework sets the "main" application class to the test
		// class, so this is a reliable way to only detect Docker when the application is
		// actually started, and never in tests.
		if (!WeatherApplication.class.equals(application.getMainApplicationClass())) {
			return;
		}

		boolean dockerRunning = false;
		try {
			Process process = new ProcessBuilder("docker", "info").redirectErrorStream(true).start();
			dockerRunning = process.waitFor() == 0;
		}
		catch (IOException | InterruptedException _) {
		}

		if (dockerRunning) {
			environment.addActiveProfile("docker");
			log.info("🐳 Running with Docker enabled");
		}
		else {
			log.info("🎣 Docker CLI not found, skipping integration.");
		}

	}

	@Override
	public int getOrder() {
		return ConfigDataEnvironmentPostProcessor.ORDER - 1;
	}

	@Override
	public void onApplicationEvent(@NonNull ApplicationEvent event) {
		log.replayTo(DockerPresentEnvironmentPostProcessor.class);
	}

}
