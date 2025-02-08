package com.ibm.cldk.analysis.interfaces;

import java.util.List;

/**
 * Abstract base class for finding CRUD operations in various frameworks.
 */
public abstract class AbstractCRUDFinder {
    public abstract boolean isCreateOperation(String receiverType, String methodName);

    public abstract boolean isDeleteOperation(String receiverType, String methodName);

    public abstract boolean isUpdateOperation(String receiverType, String methodName);

    public abstract boolean isReadOperation(String receiverType, String methodName);

    public abstract boolean isReadQuery(String declaringType, String nameAsString);

    public abstract boolean isWriteQuery(String declaringType, String nameAsString);
}
