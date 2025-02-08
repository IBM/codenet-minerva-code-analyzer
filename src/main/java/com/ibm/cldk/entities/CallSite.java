package com.ibm.cldk.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallSite {
    private String methodName;
    private String receiverExpr;
    private String receiverType;
    private List<String> argumentTypes;
    private String returnType;
    private String calleeSignature;
    // Access specifiers
    private boolean isPublic = false;
    private boolean isProtected = false;
    private boolean isPrivate = false;
    private boolean isUnspecified = false;
    private boolean isStaticCall;
    private boolean isConstructorCall;
    private CRUDOperation crudOperations = null;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
}
