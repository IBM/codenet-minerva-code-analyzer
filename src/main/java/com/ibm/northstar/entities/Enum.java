package com.ibm.northstar.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class Enum extends Type {
    private List<EnumConstant> enumConstants;
}