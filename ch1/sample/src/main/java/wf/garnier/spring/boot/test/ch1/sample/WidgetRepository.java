package wf.garnier.spring.boot.test.ch1.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
class WidgetRepository {

    private Map<Long, Widget> widgetById = new HashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public WidgetRepository() {
    }

    public List<Widget> findAll() {
        return widgetById.values().stream().collect(Collectors.toList());
    }

    public Optional<Widget> findById(Long id) {
        return Optional.ofNullable(widgetById.get(id));
    }

    public Widget createWidget(String name) {
        var newWidget = new Widget(nextId.incrementAndGet(), name);
        widgetById.put(newWidget.id(), newWidget);
        return newWidget;
    }
}
