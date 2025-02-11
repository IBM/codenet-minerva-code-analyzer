package com.ibm.cldk.entities;

import com.ibm.cldk.javaee.utils.enums.CRUDQueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRUDQuery {
    private int lineNumber = -1;
    private List<String> queryArguments;
    private CRUDQueryType queryType;
}
