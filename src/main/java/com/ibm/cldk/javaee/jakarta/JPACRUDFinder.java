package com.ibm.cldk.javaee.jakarta;

import com.ibm.cldk.javaee.utils.enums.CRUDOperationType;
import com.ibm.cldk.javaee.utils.enums.JPAQueryMethod;
import com.ibm.cldk.javaee.utils.interfaces.AbstractCRUDFinder;

import java.util.List;
import java.util.Optional;

public class JPACRUDFinder extends AbstractCRUDFinder {

    // Detect CREATE Operation
    @Override
    public boolean isCreateOperation(String receiverType, String name) {
        return receiverType.endsWith("EntityManager") && name.equals("persist");
    }

    // Detect DELETE Operation
    @Override
    public boolean isDeleteOperation(String receiverType, String name) {
        return receiverType.endsWith("EntityManager") && name.equals("remove");
    }

    // Detect UPDATE Operation, including query executions
    @Override
    public boolean isUpdateOperation(String receiverType, String name) {
        if (receiverType.endsWith("Query")) {
            Optional<CRUDOperationType> operation = JPAQueryMethod.getOperationForMethod(name);
            // There's a caveat here because UPDATE/DELETE operations are both represented by the same method.
            // See  https://github.com/codellm-devkit/codeanalyzer-java/issues/100#issuecomment-2644492440
            return operation.isPresent() && (operation.get() == CRUDOperationType.UPDATE);
        }
        return receiverType.endsWith("EntityManager") && name.equals("merge");
    }

    // Detect READ Operation, including query executions using the JPAQueryMethod enum
    @Override
    public boolean isReadOperation(String receiverType, String name) {
        if (receiverType.endsWith("EntityManager") && name.equals("find")) {
            return true;
        }

        if (receiverType.endsWith("Query")) {
            Optional<CRUDOperationType> operation = JPAQueryMethod.getOperationForMethod(name);
            return operation.isPresent() && operation.get() == CRUDOperationType.READ;
        }

        return false;
    }

    // Detect CRUD Query Creation (Only query definitions, not execution)
    @Override
    public boolean isCRUDQueryCreation(String declaringType, String methodName) {
        return declaringType.endsWith("EntityManager") &&
                (methodName.equals("createQuery") || methodName.equals("createNamedQuery"));
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isReadQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return isCRUDQueryCreation(declaringType, nameAsString) && arguments.stream().anyMatch(args -> {
            String query = args.get(0).toLowerCase();
            return query.startsWith("select");
        });
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isWriteQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return isCRUDQueryCreation(declaringType, nameAsString) && arguments.stream().anyMatch(args -> {
            String query = args.get(0).toLowerCase();
            return query.startsWith("update") || query.startsWith("delete") || query.startsWith("insert");
        });
    }

    /**
     * @param declaringType
     * @param nameAsString
     * @param arguments
     * @return
     */
    @Override
    public boolean isNamedQuery(String declaringType, String nameAsString, Optional<List<String>> arguments) {
        return declaringType.endsWith("EntityManager") && nameAsString.equals("createNamedQuery");
    }
}
