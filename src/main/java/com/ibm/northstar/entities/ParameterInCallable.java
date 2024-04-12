package com.ibm.northstar.entities;

import lombok.Data;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParameterInCallable {
    private String type;
    private String name;
    private List<String> annotations;
    private List<String> modifiers;
}
