package com.ibm.cldk.utils.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotImplemented {
    String value() default "";
    String since() default "";
    String ticketId() default "";
}