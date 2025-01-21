package pricechangesession;

import irwwbase.IRWWBase;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import itemjpa.ItemJPA;

public class PCInterceptor extends IRWWBase {
	
	private static final long serialVersionUID = -8449238441032678253L;
	
	@AroundInvoke
	public Object pcintercept(InvocationContext ctx) throws Exception {
		debugOut("*** PCInterceptor intercepting " + ctx.getMethod().getName());
		
		try {
			if (ctx.getMethod().getName().startsWith("priceChange")) {
				Object o[] = ctx.getParameters();
				ItemJPA input = (ItemJPA)o[0];
				debugOut("Intercepted input parms, attempting to change price to " + input.getItemPrice() + " for itemId " + input.getItemId());
			}
			
			return ctx.proceed();
		} finally {
			debugOut("*** PCInterceptor exiting");
		}
	}
}
