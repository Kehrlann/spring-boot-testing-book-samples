package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class InventoryApplicationRunner implements ApplicationRunner {

    private final List<Thing> things;

    public InventoryApplicationRunner(List<Thing> things) {
        this.things = things;
    }

    @Override
    public void run(ApplicationArguments args) {
        things.forEach(System.out::println);
    }
}
