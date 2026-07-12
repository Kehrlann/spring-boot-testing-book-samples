package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { PreferencesConfiguration.class })
class PreferencesPropertiesTest {

	@Autowired
	PreferencesProperties props;

	@Test
	void hasDefaults() {
		assertThat(props.getDefaults().darkMode()).isFalse();
		assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
		assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
		assertThat(props.getThreshold().cold()).isEqualTo(10);
		assertThat(props.getThreshold().hot()).isEqualTo(25);
	}

}
