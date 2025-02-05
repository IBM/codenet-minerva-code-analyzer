package com.ibm.cldk.entities;

import com.ibm.cldk.utils.annotations.NotImplemented;
import com.ibm.cldk.utils.annotations.Todo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotImplemented
public class CRUDOperation {
    public enum OperationType {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        UNKNOWN
    }

    @Todo(comment = "Add more frameworks, and consider moving this outside because this may be generic.")
    @NotImplemented
    public enum JavaFramework {
        JPA,
        SPRING
    }

    private OperationType operationType;
    private String targetTable;
    private int lineNumber;
    private int startPosition;
    private int endPosition;

    @NotImplemented
    private String operationString;
    @NotImplemented
    private List<String> involvedFields;
    @NotImplemented
    private String condition;
    @NotImplemented
    private List<String> joinedTables;
    @NotImplemented
    private JavaFramework framework;
    @NotImplemented
    private boolean isBatchOperation = false;
}