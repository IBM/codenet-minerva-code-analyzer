package com.ibm.cldk.utils.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Todo {
    String value() default "";
    String issue() default "";
    String comment() default "";
}