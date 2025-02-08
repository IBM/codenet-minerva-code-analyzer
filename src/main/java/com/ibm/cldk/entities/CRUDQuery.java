package com.ibm.cldk.entities;

import com.ibm.cldk.analysis.utils.enums.CRUDQueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRUDQuery {
    private int lineNumber = -1;
    private String query;
    private CRUDQueryType queryType;
}
