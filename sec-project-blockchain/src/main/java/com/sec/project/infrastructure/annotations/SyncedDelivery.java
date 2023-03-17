package com.sec.project.infrastructure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.sec.project.utils.Constants.DEFAULT_TIMEOUT;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SyncedDelivery {
    int value() default DEFAULT_TIMEOUT;
}
