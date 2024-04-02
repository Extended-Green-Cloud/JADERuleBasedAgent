package org.jrba.integration.jade;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.text.Element;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface RunRMA {
}
