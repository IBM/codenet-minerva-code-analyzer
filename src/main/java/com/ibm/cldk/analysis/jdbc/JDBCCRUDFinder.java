package com.ibm.cldk.analysis.jdbc;

import com.ibm.cldk.analysis.interfaces.AbstractCRUDFinder;

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
     * @param nameAsString
     * @return
     */
    @Override
    public boolean isReadQuery(String declaringType, String nameAsString) {
        return false;
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @return
     */
    @Override
    public boolean isWriteQuery(String declaringType, String nameAsString) {
        return false;
    }
}
