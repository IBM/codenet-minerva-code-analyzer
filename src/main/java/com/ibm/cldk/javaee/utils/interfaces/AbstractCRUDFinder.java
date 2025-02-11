package com.ibm.cldk.javaee.utils.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for finding CRUD operations in various frameworks.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class AbstractCRUDFinder {
    public abstract boolean isCreateOperation(String receiverType, String methodName);

    public abstract boolean isDeleteOperation(String receiverType, String methodName);

    public abstract boolean isUpdateOperation(String receiverType, String methodName);

    public abstract boolean isReadOperation(String receiverType, String methodName);

    // Detect CRUD Query Creation (Only query definitions, not execution)
    public abstract boolean isCRUDQueryCreation(String declaringType, String methodName);

    public abstract boolean isReadQuery(String declaringType, String nameAsString, Optional<List<String>> arguments);

    public abstract boolean isWriteQuery(String declaringType, String nameAsString, Optional<List<String>> arguments);

    public abstract boolean isNamedQuery(String declaringType, String nameAsString, Optional<List<String>> arguments);
}
