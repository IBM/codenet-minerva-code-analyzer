package com.ibm.cldk.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Callable {
    private String filePath;
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
    private List<CallSite> callSites;
    private List<VariableDeclaration> variableDeclarations;
    private int cyclomaticComplexity;
    private boolean isEntryPoint = false;
    private List<CRUDOperation> crudOperations = null;
}
