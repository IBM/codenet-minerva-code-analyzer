package org.entrypoints.struts;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class InterceptorExample extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // Pre-processing
        String result = invocation.invoke();
        // Post-processing
        return result;
    }
}
