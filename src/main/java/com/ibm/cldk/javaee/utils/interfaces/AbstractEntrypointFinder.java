package com.ibm.cldk.javaee.utils.interfaces;

import com.ibm.cldk.utils.annotations.NotImplemented;

@NotImplemented(comment = "This class is not implemented yet. Leaving this here to refactor entrypoint detection.")
public abstract class AbstractEntrypointFinder {
    /**
     * Enum for rules.
     */
    enum Rulest{
    }

    /**
     * Detect if the method is an entrypoint.
     *
     * @param receiverType The type of the receiver object.
     * @param name The name of the method.
     * @return True if the method is an entrypoint, false otherwise.
     */
    public abstract boolean isEntrypointClass(String receiverType, String name);

    public abstract boolean isEntrypointMethod(String receiverType, String name);
}
