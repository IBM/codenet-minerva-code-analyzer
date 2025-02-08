package com.ibm.cldk.entities;

import com.ibm.cldk.javaee.utils.enums.CRUDOperationType;
import com.ibm.cldk.utils.annotations.NotImplemented;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRUDOperation {
    private int lineNumber = -1;
    private CRUDOperationType operationType;

    @NotImplemented
    private String targetTable = null;
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

    public CRUDOperation(int lineNumber, CRUDOperationType crudOperationType) {
        this.lineNumber = lineNumber;
        this.operationType = crudOperationType;
    }
}