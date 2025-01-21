package decorators;

import java.lang.annotation.Retention;

import jakarta.inject.Qualifier;
import java.lang.annotation.*;


@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecoratorQualifier {

}