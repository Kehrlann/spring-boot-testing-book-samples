package wf.garnier.spring.boot.test.ch3.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {InventoryApplicationRunner.class})
@ExtendWith(OutputCaptureExtension.class)
@Import(TestSpecificConfiguration.class)
class ImportTests {

    @Test
    void contextLoads(CapturedOutput output) {
        assertThat(output)
                .contains(
                        "Thing[name=configuration-test-package]"
                );
    }
}
