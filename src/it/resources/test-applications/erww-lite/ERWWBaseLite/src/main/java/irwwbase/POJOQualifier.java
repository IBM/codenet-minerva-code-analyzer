package irwwbase;

import jakarta.inject.*;
import java.lang.annotation.*;

@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface POJOQualifier {
	//nothing to do here
}