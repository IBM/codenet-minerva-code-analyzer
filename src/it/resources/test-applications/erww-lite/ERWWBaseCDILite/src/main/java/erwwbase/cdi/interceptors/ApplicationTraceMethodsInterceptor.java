package erwwbase.cdi.interceptors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@ApplicationTraceInterceptor
@Interceptor @Priority(1)
public class ApplicationTraceMethodsInterceptor {
    @AroundInvoke
    public Object trace(InvocationContext context) throws Exception { 
  
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In ERWW-Lite Trace: " + context.getMethod().getDeclaringClass().getName()
						+ ": " + context.getMethod().getName()); 		
        return context.proceed(); 
    }
}
