package wf.garnier.spring.boot.test.ch1.widget.test.boot;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch1.widget.InvalidWidgetException;
import wf.garnier.spring.boot.test.ch1.widget.WidgetRepository;
import wf.garnier.spring.boot.test.ch1.widget.WidgetValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Here is an example using the well-known {@link SpringBootTest} annotation. This is the
 * type of test you should be writing.
 */
// tag::content[]
// tag::annotations[]
@SpringBootTest(properties = "widget.id.step=5") // <1>
@AutoConfigureMockMvc // <2>
// end::annotations[]
class AwesomeSpringBootTests {

	// tag::class-members[]
	@Autowired
	private MockMvc mockMvc; // <2>

	@Autowired
	private WidgetRepository repository; // <3>

	@MockitoBean
	private WidgetValidator mockValidator; // <4>

	// end::class-members[]
	// tag::test[]
	@Test
	void addWidget() throws Exception {
		doNothing().when(mockValidator).validateWidget(anyString()); // <1>

		//@formatter:off
		var location = mockMvc.perform(  // <2>
				post("/widget").param("name", "test-widget") // <2>
			)
			.andExpect(status().isCreated()) // <3>
			.andReturn()
			.getResponse()
			.getHeader("location"); // <4>
		//@formatter:on

		var id = getWidgetId(location);
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget"); // <5>
	}

	// end::test[]
	// tag::ignored[]
	@Test
	void addWidgetRejected() throws Exception {
		doThrow(new InvalidWidgetException("rejected")).when(mockValidator).validateWidget(anyString());
		var widgetCount = repository.count();

		mockMvc.perform(post("/widget").param("name", "test-widget")).andExpect(status().isBadRequest());
		assertThat(repository.count()).isEqualTo(widgetCount);
	}

	@Test
	void widgetIdIncrementsWithStep() throws Exception {
		doNothing().when(mockValidator).validateWidget(anyString());
		var firstLocation = mockMvc.perform(post("/widget").param("name", "test-widget"))
			.andReturn()
			.getResponse()
			.getHeader("location");

		var firstId = getWidgetId(firstLocation);
		var secondLocation = mockMvc.perform(post("/widget").param("name", "test-widget"))
			.andReturn()
			.getResponse()
			.getHeader("location");

		var secondId = getWidgetId(secondLocation);
		assertThat(secondId - firstId).isEqualTo(5);
	}

	private static int getWidgetId(String location) {
		//@formatter:off
		var id = UriComponentsBuilder.fromUriString(location)
				.build()
				.getPathSegments()
				.getLast();
		//@formatter:on
		return Integer.parseInt(id);
	}
	// end::ignored[]

}
// end::content[]