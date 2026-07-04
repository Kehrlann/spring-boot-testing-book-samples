package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test showing a custom {@link Profile}.
 */
@SpringBootTest
@ActiveProfiles("custom")
class ProfileTests {

	@Test
	void things(@Autowired List<Thing> things) {
		var names = things.stream().map(Thing::name);
		assertThat(names).containsExactlyInAnyOrder("red", "pink", "green", "magenta", "orange");
	}

}
