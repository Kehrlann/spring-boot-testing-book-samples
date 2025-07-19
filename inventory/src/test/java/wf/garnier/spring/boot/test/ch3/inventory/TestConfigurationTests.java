package wf.garnier.spring.boot.test.ch3.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class TestConfigurationTests {

    @Test
    void thing(CapturedOutput output) {
        assertThat(output).contains(
                "Thing[name=one]",
                "Thing[name=two]",
                "Thing[name=testconfig]",
                "Thing[name=configuration-test-package]"
        );
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        Thing testConfigThing() {
            return new Thing("testconfig");
        }
    }
}
