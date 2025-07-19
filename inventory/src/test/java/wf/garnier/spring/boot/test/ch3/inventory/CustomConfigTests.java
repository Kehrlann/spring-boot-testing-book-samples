package wf.garnier.spring.boot.test.ch3.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {InventoryApplicationRunner.class, ThingConfiguration.class, CustomConfigTests.TestConfig.class})
@ExtendWith(OutputCaptureExtension.class)
class CustomConfigTests {

    @Test
    void contextLoads(CapturedOutput output) {
        assertThat(output)
                .contains(
                        "Thing[name=one]",
                        "Thing[name=two]",
                        "Thing[name=test]"
                )
                .doesNotContain(
                        "Thing[name=configuration-test-package]"
                );
    }

    @Configuration
    static class TestConfig {

        @Bean
        public Thing three() {
            return new Thing("test");
        }

    }
}
