package com.ibm.northstar.entities;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class JavaCompilationUnit {
    private String filePath;
    private String comment;
    private List<String> imports;
    private Map<String, Type> typeDeclarations;
}
