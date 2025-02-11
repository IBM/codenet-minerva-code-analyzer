package com.ibm.cldk.javaee.utils.enums;

import com.ibm.cldk.utils.annotations.Note;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum JPAQueryMethod {
    // Read Operations
    GET_RESULT_LIST(CRUDOperationType.READ),
    GET_SINGLE_RESULT(CRUDOperationType.READ),
    GET_FIRST_RESULT(CRUDOperationType.READ),
    GET_MAX_RESULTS(CRUDOperationType.READ),

    // Write Operations
    @Note("There is a possiblity that the user may execute a delete action using the executeUpdate method. There is no way to differentiate between an update and delete operation without doing a dataflow analysis on the query string because the query string may be defined anywhere in the code. So for now, we are assuming that executeUpdate is an update operation.")
    EXECUTE_UPDATE(CRUDOperationType.UPDATE),

    // Non-CRUD Methods (configuration or metadata)
    GET_FLUSH_MODE(null),
    GET_HINTS(null),
    GET_LOCK_MODE(null),
    GET_PARAMETER(null),
    GET_PARAMETERS(null),
    GET_PARAMETER_VALUE(null),
    IS_BOUND(null),
    UNWRAP(null);

    private final CRUDOperationType crudOperation;

    JPAQueryMethod(CRUDOperationType crudOperation) {
        this.crudOperation = crudOperation;
    }

    public static Optional<CRUDOperationType> getOperationForMethod(String methodName) {
        try {
            // A small hack to convert camelCase to snake_case and also to uppercase. Basically, we want getSingleResult to get converted to GET_SINGLE_RESULT so as to
            // match the enum values conventions.
            return Optional.ofNullable(JPAQueryMethod.valueOf(methodName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase()).getCrudOperation());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
