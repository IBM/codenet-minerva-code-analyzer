package com.ibm.cldk.javaee.struts;

import com.ibm.cldk.javaee.utils.interfaces.AbstractEntrypointFinder;

public class StrutsEntrypointFinder extends AbstractEntrypointFinder {
    @Override
    public boolean isEntrypointClass(String receiverType, String name) {
        return false;
    }

    @Override
    public boolean isEntrypointMethod(String receiverType, String name) {
        return false;
    }
}
