package com.ibm.northstar.entities;

import lombok.Data;

import java.util.List;

@Data
public class VariableDeclaration {
    private String name;
    private String type;
    private String initializer;
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
}
