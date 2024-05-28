package com.ibm.northstar.entities;

import lombok.Data;

import java.util.List;

@Data
public class CallSite {
    private String methodName;
    private String declaringType;
    private List<String> argumentTypes;
    private boolean isStaticCall;
    private boolean isConstructorCall;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
}
