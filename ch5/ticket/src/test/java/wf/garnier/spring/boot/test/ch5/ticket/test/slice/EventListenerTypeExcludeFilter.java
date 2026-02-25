package wf.garnier.spring.boot.test.ch5.ticket.test.slice;

import java.io.IOException;
import java.util.Set;

import org.springframework.boot.test.context.filter.annotation.StandardAnnotationCustomizableTypeExcludeFilter;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.repository.Repository;

public class EventListenerTypeExcludeFilter extends StandardAnnotationCustomizableTypeExcludeFilter<EventListenerTest> {

	private static final Set<Class<?>> COMPONENT_INCLUDES = Set.of(Repository.class);

	EventListenerTypeExcludeFilter(Class<?> testClass) {
		super(testClass);
	}

	@Override
	protected Set<Class<?>> getComponentIncludes() {
		return COMPONENT_INCLUDES;
	}

	@Override
	protected boolean defaultInclude(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		if (isTypeOrAnnotated(metadataReader, metadataReaderFactory, Repository.class)) {
			return true;
		}
		return metadataReader.getAnnotationMetadata().hasAnnotatedMethods(EventListener.class.getName());
	}

}
