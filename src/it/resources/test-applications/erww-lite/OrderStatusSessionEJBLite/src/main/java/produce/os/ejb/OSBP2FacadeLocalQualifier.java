package produce.os.ejb;

import jakarta.inject.*;
import java.lang.annotation.*;

@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OSBP2FacadeLocalQualifier {
	// nothing to do here
}
