package com.ibm.cldk.javaee.jdbc;

import com.ibm.cldk.javaee.utils.interfaces.AbstractCRUDFinder;

import java.util.List;
import java.util.Optional;

public class JDBCCRUDFinder extends AbstractCRUDFinder {
    /**
     * Detect if the method call is a create operation.
     *
     * @param receiverType
     * @param name
     * @return
     */
    @Override
    public boolean isCreateOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is a delete operation.
     *
     * @param receiverType
     * @param name
     * @return
     */
    @Override
    public boolean isDeleteOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is an update operation.
     *
     * @param receiverType
     * @param name
     * @return
     */
    @Override
    public boolean isUpdateOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is a read operation.
     *
     * @param receiverType
     * @param name
     * @return
     */
    @Override
    public boolean isReadOperation(String receiverType, String name) {
        return false;
    }

    /**
     * @param declaringType
     * @param methodName
     * @return
     */
    @Override
    public boolean isCRUDQueryCreation(String declaringType, String methodName) {
        return false;
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isReadQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return false;
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isWriteQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return false;
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isNamedQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return false;
    }
}
