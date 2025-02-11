package com.ibm.cldk.utils.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.MODULE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Note {
    String value() default "";
}
