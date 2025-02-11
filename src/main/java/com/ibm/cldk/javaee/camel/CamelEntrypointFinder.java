package com.ibm.cldk.javaee.camel;

import com.ibm.cldk.javaee.utils.interfaces.AbstractEntrypointFinder;
import com.ibm.cldk.utils.annotations.NotImplemented;

@NotImplemented(comment = "This class is not implemented yet. Leaving this here to refactor entrypoint detection.")
public class CamelEntrypointFinder extends AbstractEntrypointFinder {
    @Override
    public boolean isEntrypointClass(String receiverType, String name) {
        return false;
    }

    @Override
    public boolean isEntrypointMethod(String receiverType, String name) {
        return false;
    }
}
