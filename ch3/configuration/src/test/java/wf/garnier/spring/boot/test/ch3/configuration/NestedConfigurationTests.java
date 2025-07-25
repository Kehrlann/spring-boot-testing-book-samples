package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NestedConfigurationTests {

	@Test
	void things(@Autowired List<Thing> things) {
		assertThat(things).map(Thing::name)
			.containsExactly("nested-configuration-bean")
			.doesNotContain("bean-one", "bean-two");
	}

	@Test
	void properties(@Autowired Optional<DemoProperties> demoProperties) {
		assertThat(demoProperties).isEmpty();
	}

	@Configuration
	static class TestConfig {

		@Bean
		Thing testConfigThing() {
			return new Thing("nested-configuration-bean");
		}

	}

}
