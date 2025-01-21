package erwwbase.bean.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = {OrderInputParameterConstraintValidator.class}) 
@Retention(RetentionPolicy.RUNTIME) 
@Target(value={ElementType.PARAMETER})
public @interface OrderInputParameterConstraint {
	Class<?>[] groups() default {}; 
	Class<? extends Payload>[] payload() default {}; 
	String message() default "The warehouseId, districtId and customerId must be an integer."; 
} 
