package com.ibm.cldk.analysis.struts;

import com.ibm.cldk.analysis.interfaces.AbstractEntrypointFinder;

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
