package com.ibm.cldk.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;

import java.util.Map;

import static com.ibm.cldk.CodeAnalyzer.gson;

@Data
@EqualsAndHashCode(callSuper = true)
public class CallableVertex extends AbstractGraphVertex {
    private String filePath;
    private String typeDeclaration;
    private String signature;
    private String callableDeclaration;

    public CallableVertex(Map<String, String> callable) {
        this.filePath = callable.get("filePath");
        this.typeDeclaration = callable.get("typeDeclaration");
        this.signature = callable.get("signature");
        this.callableDeclaration = callable.get("callableDeclaration");
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    @Override
    public Map<String, Attribute> getAttributes() {
        return Map.ofEntries(
                Map.entry("filePath", DefaultAttribute.createAttribute(getFilePath())),
                Map.entry("typeDeclaration", DefaultAttribute.createAttribute(getTypeDeclaration())),
                Map.entry("signature", DefaultAttribute.createAttribute(getSignature())),
                Map.entry("callableDeclaration", DefaultAttribute.createAttribute(getCallableDeclaration())
                )
        );
    }

}
