package wf.garnier.spring.boot.test.ch4.weather;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * These tests showcase that {@link MockMvc} and its derivatives run on the same thread as
 * the test, but that when you use a full servlet container, like Tomcat, this is not the
 * case.
 */
class MockMvcInternalsTests {

	@Nested
	@SpringBootTest
	@AutoConfigureMockMvc
	class MockMvcTests {

		static ThreadLocal<String> localValue = new ThreadLocal<>();

		@Autowired
		MockMvcTester mvc;

		@AfterEach
		void tearDown() {
			localValue.remove();
		}

		@Test
		void helloWorld() {
			mvc.get().uri("/").exchange().assertThat().bodyText().isEqualTo("Hello World");
		}

		@Test
		void useLocalValue() {
			localValue.set("Bonjour Monde");
			mvc.get().uri("/").exchange().assertThat().bodyText().isEqualTo("Bonjour Monde");
		}

		@Configuration
		static class TestConfig {

			/**
			 * Instead of using a {@link Controller} component, we use the lightweight
			 * {@code WebMvc.fn} framework provided by Spring. It gives us a nice
			 * one-liner instead of having another class.
			 * <p>
			 * In that function, we access a thread-local from the test itself.
			 * @see <a href=
			 * "https://docs.spring.io/spring-framework/reference/web/webmvc-functional.html">Function
			 * Endpoints</a> in the reference docs.
			 */
			@Bean
			RouterFunction<ServerResponse> server() {
				return route().GET("/", request -> {
					var valueFromThreadLocal = localValue.get();
					if (valueFromThreadLocal != null) {
						return ServerResponse.ok().body(valueFromThreadLocal);
					}
					return ServerResponse.ok().body("Hello World");
				}).build();
			}

		}

	}

	@Nested
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	class TomcatTests {

		@LocalServerPort
		int port;

		static ThreadLocal<String> localValue = new ThreadLocal<>();

		RestClient client = RestClient.create();

		@AfterEach
		void tearDown() {
			localValue.remove();
		}

		@Test
		void helloWorld() {
			var response = client.get().uri("http://localhost:" + port).retrieve().body(String.class);
			assertThat(response).isEqualTo("Hello World");
		}

		@Test
		void useLocalValue() {
			localValue.set("Bonjour Monde");
			var response = client.get().uri("http://localhost:" + port).retrieve().body(String.class);
			// The test and the server run on separate threads, so the server's thread
			// local is different
			// from the test's.
			assertThat(response).isEqualTo("Hello World");
		}

		@Configuration
		@EnableWebMvc
		@ImportAutoConfiguration(value = { WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
				ServletWebServerFactoryAutoConfiguration.class })
		static class TestConfig {

			/**
			 * Instead of using a {@link Controller} component, we use the lightweight
			 * {@code WebMvc.fn} framework provided by Spring. It gives us a nice
			 * one-liner instead of having another class.
			 * <p>
			 * In that function, we access a thread-local from the test itself.
			 * @see <a href=
			 * "https://docs.spring.io/spring-framework/reference/web/webmvc-functional.html">Function
			 * Endpoints</a> in the reference docs.
			 */
			@Bean
			RouterFunction<ServerResponse> server() {
				return route().GET("**", request -> {
					var valueFromThreadLocal = localValue.get();
					if (valueFromThreadLocal != null) {
						return ServerResponse.ok().body(valueFromThreadLocal);
					}
					return ServerResponse.ok().body("Hello World");
				}).build();
			}

		}

	}

}
