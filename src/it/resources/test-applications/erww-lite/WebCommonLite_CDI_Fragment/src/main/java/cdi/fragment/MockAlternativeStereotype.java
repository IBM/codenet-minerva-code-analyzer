package cdi.fragment;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Stereotype;

@Stereotype
@Alternative
@RequestScoped
@Retention(RUNTIME)
@Target(TYPE)
public @interface MockAlternativeStereotype {

}
