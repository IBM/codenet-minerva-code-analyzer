package com.ibm.cldk.entities;

import com.ibm.cldk.utils.annotations.NotImplemented;
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
    private String technology;
    @NotImplemented
    private boolean isBatchOperation = false;
}