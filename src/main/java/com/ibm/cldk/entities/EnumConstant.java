package com.ibm.cldk.entities;

import lombok.Data;

import java.util.List;

@Data
public class EnumConstant {
    private String name;
    private List<String> arguments;
}
