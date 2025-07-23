package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { ThingApplicationRunner.class })
@Import(TestSpecificConfiguration.class)
class ImportTests {

	@Test
	void things(@Autowired List<Thing> things) {
		assertThat(things).map(Thing::name)
			.containsExactly("configuration-test-package")
			.doesNotContain("bean-one", "bean-two");
	}

	@Test
	void properties(@Autowired Optional<DemoProperties> demoProperties) {
		assertThat(demoProperties).isEmpty();
	}

}
