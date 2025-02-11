package com.ibm.cldk.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Type {
    private boolean isNestedType;
    private boolean isClassOrInterfaceDeclaration;
    private boolean isEnumDeclaration;
    private boolean isAnnotationDeclaration;
    private boolean isRecordDeclaration;
    private boolean isInterface;
    private boolean isInnerClass;
    private boolean isLocalClass;
    private List<String> extendsList = new ArrayList<>();
    private String comment;
    private List<String> implementsList = new ArrayList<>();
    private List<String> modifiers = new ArrayList<>();
    private List<String> annotations = new ArrayList<>();
    private String parentType;
    private List<String> nestedTypeDeclarations = new ArrayList<>();
    private Map<String, Callable> callableDeclarations = new HashMap<>();
    private List<Field> fieldDeclarations = new ArrayList<>();
    private List<EnumConstant> enumConstants = new ArrayList<>();
    private boolean isEntrypointClass = false;
}