package wf.garnier.spring.boot.test.ch3.contextcache;

import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import wf.garnier.spring.boot.test.ch3.configuration.Gizmo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * We run these tests in order, to ensure the {@link SlowTests} start first, and get the
 * spring startup logs.
 */
// tag::slow-tests[]
@TestClassOrder(ClassOrderer.OrderAnnotation.class) // <1>
class SlowApplicationTests {

	// tag::ignored[]
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	// end::ignored[]
	@Order(1) // <1>
	@SpringBootTest
	class SlowTests {

		// tag::ignored[]
		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running SlowTests");
		}

		// end::ignored[]
		@Test
		void contextLoads() {
			// tag::ignored[]
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
			// end::ignored[]
		}

	}

	// tag::ignored[]
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	// end::ignored[]
	@Order(2) // <1>
	@SpringBootTest
	class FastTests {

		// tag::ignored[]
		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running FastTests");
		}

		// end::ignored[]
		@Test
		void contextLoads() {
			// tag::ignored[]
		}

		@Test
		void doesNotLogAppStartup(CapturedOutput output) {
			assertThat(output).doesNotContain(">>> Configuring slow bean...");
			// end::ignored[]
		}

	}
	// tag::ignored[]

	/**
	 * Slow tests: different properties
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(3)
	@SpringBootTest(properties = { "some.property=42", "startup.delay=100ms" })
	class PropertiesTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running PropertiesTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: different properties. Same values as {@link PropertiesTests}, but
	 * different source.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(4)
	@SpringBootTest
	@TestPropertySource(properties = { "some.property=42", "startup.delay=100ms" })
	class PropertySourcesTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running PropertySourcesTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: different profile.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(5)
	@SpringBootTest(properties = "startup.delay=100ms")
	@ActiveProfiles("custom")
	class ProfileTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running ProfileTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: different classes.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(6)
	@SpringBootTest(classes = { SlowApplication.class, Gizmo.class }, properties = "startup.delay=100ms")
	class ClassesTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running ClassesTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: nested configuration.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(7)
	@SpringBootTest(properties = "startup.delay=100ms")
	class NestedConfigurationTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running NestedConfigurationTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

		@TestConfiguration
		static class NestedConfig {

			// Even empty configs throw off the cache!

		}

	}

	/**
	 * Slow tests: extra auto-configuration.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(8)
	@SpringBootTest(properties = "startup.delay=100ms")
	@AutoConfigureMockMvc
	class ExtraAutoConfigurationTests {

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running ExtraAutoConfigurationTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: mockito beans
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(9)
	@SpringBootTest(properties = "startup.delay=100ms")
	class MockitoBeanTests {

		@MockitoBean
		SlowApplication.FastBean fastBean;

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running MockitoBeanTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

	}

	/**
	 * Slow tests: test beans
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(10)
	@SpringBootTest(properties = "startup.delay=100ms")
	class TestBeanTests {

		@TestBean
		SlowApplication.FastBean fastBean;

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running TestBeanTests");
		}

		@Test
		void logsAppStartup(CapturedOutput output) {
			assertThat(output).contains(">>> Configuring slow bean...");
		}

		static SlowApplication.FastBean fastBean() {
			return new SlowApplication.FastBean();
		}

	}

	/**
	 * Fast tests! Read-only access to things in the context. Reuses the context from
	 * {@link SlowTests}.
	 */
	@Nested
	@ExtendWith(OutputCaptureExtension.class)
	@Order(11)
	@SpringBootTest
	class ReadOnlyTests {

		@Autowired
		SlowApplication.FastBean fastBean;

		@Value("${startup.delay:0ms}")
		Duration sleepDuration;

		@BeforeAll
		static void logStart() {
			System.out.println("\n🧪 Running TestBeanTests");
		}

		@Test
		void doesNotLogAppStartup(CapturedOutput output) {
			assertThat(output).doesNotContain(">>> Configuring slow bean...");
		}

	}

	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@Order(12)
	@CustomSpringTest
	class MetaAnnotationTests {

		@Order(1)
		@Test
		void makeStuff() {

		}

		@Order(2)
		// tag::dirties-context[]
		@Test
		@DirtiesContext
		void breakStuff() {
			// ... some destructive test code ...
		}
		// end::dirties-context[]

		@Order(3)
		@Test
		void doStuff() {
		}

		@Order(4)
		// tag::dirties-context-before[]
		@Test
		@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
		void requireCleanSlate() {
			// ...
		}
		// end::dirties-context-before[]

	}
	// end::ignored[]

}
// end::slow-tests[]
