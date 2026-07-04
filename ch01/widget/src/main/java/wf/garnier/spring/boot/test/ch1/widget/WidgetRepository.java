package wf.garnier.spring.boot.test.ch1.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class WidgetRepository {

	private final Map<Integer, Widget> widgetById = new HashMap<>();

	private final AtomicInteger nextId = new AtomicInteger(1);

	private final int step;

	public WidgetRepository(@Value("${widget.id.step}") int step) {
		this.step = step;
	}

	public List<Widget> findAll() {
		return new ArrayList<>(widgetById.values());
	}

	public Optional<Widget> findById(int id) {
		return Optional.ofNullable(widgetById.get(id));
	}

	public Widget createWidget(String name) {
		var newWidget = new Widget(nextId.getAndAdd(step), name);
		widgetById.put(newWidget.id(), newWidget);
		return newWidget;
	}

	public int count() {
		return widgetById.size();
	}

}
