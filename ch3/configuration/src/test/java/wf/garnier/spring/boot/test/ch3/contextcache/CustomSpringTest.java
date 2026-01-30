package wf.garnier.spring.boot.test.ch3.contextcache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import wf.garnier.spring.boot.test.ch3.configuration.Gizmo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// tag::custom-spring-test[]
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(properties = "startup.delay=100ms") <1>
@MockitoBean(types = { SlowApplication.FastBean.class, Gizmo.class }) <2>
@interface CustomSpringTest { <3>

}
// end::custom-spring-test[]
