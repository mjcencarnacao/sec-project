package com.sec.project.infrastructure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Java annotation that will be used to annotate methods where a Byzantine node can mess with the data.
 * The logic of this is specified in the respecting aspect.
 *
 * @see com.sec.project.infrastructure.aspects.ByzantineAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Byzantine {
}
