package com.ibm.northstar.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class ClassOrInterface extends Type {
    private boolean isInterface;
    private boolean isInnerClass;
    private boolean isLocalClass;
    private List<String> extendsList;
}