package com.ibm.cldk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodeAnalyzerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testOutputJSONShape() {

    }

    private boolean compareJsonStructure(JsonElement referenceElement, JsonElement actualElement) {
        if (referenceElement.isJsonObject() && actualElement.isJsonObject()) {
            JsonObject referenceObject = referenceElement.getAsJsonObject();
            JsonObject actualObject = actualElement.getAsJsonObject();

            // Ensure that all keys in the reference JSON are present in the actual JSON
            for (String key : referenceObject.keySet()) {
                if (!actualObject.has(key)) {
                    return false;
                }
                // Recursively compare the child elements
                if (!compareJsonStructure(referenceObject.get(key), actualObject.get(key))) {
                    return false;
                }
            }
        } else if (referenceElement.isJsonArray() && actualElement.isJsonArray()) {
            // If both are arrays, compare their elements
            JsonArray referenceArray = referenceElement.getAsJsonArray();
            JsonArray actualArray = actualElement.getAsJsonArray();
            if (referenceArray.size() != actualArray.size()) {
                return false;
            }
            for (int i = 0; i < referenceArray.size(); i++) {
                if (!compareJsonStructure(referenceArray.get(i), actualArray.get(i))) {
                    return false;
                }
            }
        }
        // If neither is an object or array, just return true (for primitives)
        return true;
    }
}