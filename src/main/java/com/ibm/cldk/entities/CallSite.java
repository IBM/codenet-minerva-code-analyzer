package com.ibm.cldk.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
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
    private CRUDOperation crudOperation = null;
    private CRUDQuery crudQuery = null;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
}
