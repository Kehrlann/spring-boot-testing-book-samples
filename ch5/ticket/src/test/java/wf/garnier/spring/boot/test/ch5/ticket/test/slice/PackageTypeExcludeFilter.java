package wf.garnier.spring.boot.test.ch5.ticket.test.slice;

import java.io.IOException;
import java.util.Set;

import org.springframework.boot.test.context.filter.annotation.StandardAnnotationCustomizableTypeExcludeFilter;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

public class PackageTypeExcludeFilter extends StandardAnnotationCustomizableTypeExcludeFilter<PackageTest> {

	private final String testPackage;

	PackageTypeExcludeFilter(Class<?> testClass) {
		super(testClass);
		this.testPackage = testClass.getPackageName();
	}

	@Override
	protected Set<Class<?>> getComponentIncludes() {
		return Set.of();
	}

	@Override
	protected boolean defaultInclude(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		String className = metadataReader.getClassMetadata().getClassName();
		return className.startsWith(testPackage);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		PackageTypeExcludeFilter other = (PackageTypeExcludeFilter) obj;
		return this.testPackage.equals(other.testPackage);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.testPackage.hashCode();
	}

}
