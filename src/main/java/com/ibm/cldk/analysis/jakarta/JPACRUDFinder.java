package com.ibm.cldk.analysis.jakarta;

import com.ibm.cldk.analysis.interfaces.AbstractCRUDFinder;

public class JPACRUDFinder extends AbstractCRUDFinder {
    /**
     * Detect if the method call is a create operation.
     *
     * @param receiverType The type of the receiver object.
     * @param name The name of the method.
     * @return True if the method call is a create operation, false otherwise.
     */
    @Override
    public boolean isCreateOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is a delete operation.
     *
     * @param receiverType The type of the receiver object.
     * @param name The name of the method.
     * @return True if the method call is a delete operation, false otherwise.
     */
    @Override
    public boolean isDeleteOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is an update operation.
     *
     * @param receiverType The type of the receiver object.
     * @param name The name of the method.
     * @return True if the method call is an update operation, false otherwise.
     */
    @Override
    public boolean isUpdateOperation(String receiverType, String name) {
        return false;
    }

    /**
     * Detect if the method call is a read operation.
     *
     * @param receiverType The type of the receiver object.
     * @param name The name of the method.
     * @return True if the method call is a read operation, false otherwise.
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
