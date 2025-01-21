package produce.ejb;

import jakarta.inject.*;
import java.lang.annotation.*;

@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PCEntityManagerQualifier {
	//nothing to do here
}
