package wf.garnier.spring.boot.test.ch7.weather.utils;

import java.io.IOException;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.test.context.TestPropertySource;

/**
 * Load properties in {@code yaml} format using Spring's {@link TestPropertySource}.
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
		var sourceName = name != null ? name : resource.getResource().getFilename();
		var propertySources = new YamlPropertySourceLoader().load(sourceName, resource.getResource());
		return propertySources.get(0);
	}

}
