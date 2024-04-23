package com.ibm.northstar.entities;

import lombok.Data;

import java.util.List;

@Data
public class Callable {
    private String signature;
    private String comment;
    private List<String> annotations;
    private List<String> modifiers;
    private List<String> thrownExceptions;
    private String declaration;
    private List<ParameterInCallable> parameters;
    private String code;
    private int startLine;
    private int endLine;
    private String returnType = null;
    private boolean isImplicit = false;
    private boolean isConstructor = false;
    private List<String> referencedTypes;
    private List<String> accessedFields;
    private List<String> calledMethodDeclaringTypes;
}
