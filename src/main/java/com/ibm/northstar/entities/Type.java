package com.ibm.northstar.entities;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Type {
    private boolean isNestedType;
    private boolean isClassOrInterfaceDeclaration;
    private boolean isEnumDeclaration;
    private boolean isAnnotationDeclaration;
    private boolean isRecordDeclaration;
    private String comment;
    private List<String> implementsList;
    private List<String> modifiers;
    private List<String> annotations;
    private String parentType;
    private List<String> nestedTypeDeclarations;
    private Map<String, Callable> callableDeclarations;
    private List<Field> fieldDeclarations;
}