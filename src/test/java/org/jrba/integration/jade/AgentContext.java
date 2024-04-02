package org.jrba.integration.jade;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface AgentContext {

	String agentClass();
	String agentName();
	int order() default 0;
	String parametersConstructor() default "";
}
