package com.ibm.northstar.entities;

import lombok.Data;
import java.util.List;

@Data
public class Field {
    private String comment;
    private String name;
    private String type;
    private Integer startLine;
    private Integer endLine;
    private List<String> variables;
    private List<String> modifiers;
    private List<String> annotations;
}
