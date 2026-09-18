package wf.garnier.spring.boot.test.ch7.weather;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Trick class to detect whether docker is running on the local machine.
 */
public class DockerPresentEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	private static final Logger log = LoggerFactory.getLogger(DockerPresentEnvironmentPostProcessor.class);

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

}
