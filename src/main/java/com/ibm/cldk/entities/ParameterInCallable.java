package com.ibm.cldk.entities;

import lombok.Data;

import java.util.List;

@Data
public class ParameterInCallable {
    private String type;
    private String name;
    private List<String> annotations;
    private List<String> modifiers;
}
