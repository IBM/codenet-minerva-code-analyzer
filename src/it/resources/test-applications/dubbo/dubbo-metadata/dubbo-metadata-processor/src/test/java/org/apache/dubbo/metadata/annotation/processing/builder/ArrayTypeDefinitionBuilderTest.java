/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.metadata.annotation.processing.builder;

import org.apache.dubbo.metadata.annotation.processing.AbstractAnnotationProcessingTest;
import org.apache.dubbo.metadata.annotation.processing.model.ArrayTypeModel;
import org.apache.dubbo.metadata.definition.model.TypeDefinition;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.apache.dubbo.metadata.annotation.processing.util.FieldUtils.findField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ArrayTypeDefinitionBuilder} Test
 *
 * @since 2.7.6
 */
class ArrayTypeDefinitionBuilderTest extends AbstractAnnotationProcessingTest {

    private ArrayTypeDefinitionBuilder builder;

    private TypeElement testType;

    private VariableElement integersField;

    private VariableElement stringsField;

    private VariableElement primitiveTypeModelsField;

    private VariableElement modelsField;

    private VariableElement colorsField;

    @Override
    protected void addCompiledClasses(Set<Class<?>> classesToBeCompiled) {
        classesToBeCompiled.add(ArrayTypeModel.class);
    }

    @Override
    protected void beforeEach() {
        builder = new ArrayTypeDefinitionBuilder();
        testType = getType(ArrayTypeModel.class);
        integersField = findField(testType, "integers");
        stringsField = findField(testType, "strings");
        primitiveTypeModelsField = findField(testType, "primitiveTypeModels");
        modelsField = findField(testType, "models");
        colorsField = findField(testType, "colors");
    }

    @Test
    void testAccept() {
        assertTrue(builder.accept(processingEnv, integersField.asType()));
        assertTrue(builder.accept(processingEnv, stringsField.asType()));
        assertTrue(builder.accept(processingEnv, primitiveTypeModelsField.asType()));
        assertTrue(builder.accept(processingEnv, modelsField.asType()));
        assertTrue(builder.accept(processingEnv, colorsField.asType()));
    }

    @Test
    void testBuild() {

        buildAndAssertTypeDefinition(processingEnv, integersField, "int[]", "int", builder);

        buildAndAssertTypeDefinition(processingEnv, stringsField, "java.lang.String[]", "java.lang.String", builder);

        buildAndAssertTypeDefinition(
                processingEnv,
                primitiveTypeModelsField,
                "org.apache.dubbo.metadata.annotation.processing.model.PrimitiveTypeModel[]",
                "org.apache.dubbo.metadata.annotation.processing.model.PrimitiveTypeModel",
                builder);

        buildAndAssertTypeDefinition(
                processingEnv,
                modelsField,
                "org.apache.dubbo.metadata.annotation.processing.model.Model[]",
                "org.apache.dubbo.metadata.annotation.processing.model.Model",
                builder,
                (def, subDef) -> {
                    TypeElement subType = elements.getTypeElement(subDef.getType());
                    assertEquals(ElementKind.CLASS, subType.getKind());
                });

        buildAndAssertTypeDefinition(
                processingEnv,
                colorsField,
                "org.apache.dubbo.metadata.annotation.processing.model.Color[]",
                "org.apache.dubbo.metadata.annotation.processing.model.Color",
                builder,
                (def, subDef) -> {
                    TypeElement subType = elements.getTypeElement(subDef.getType());
                    assertEquals(ElementKind.ENUM, subType.getKind());
                });
    }

    static void buildAndAssertTypeDefinition(
            ProcessingEnvironment processingEnv,
            VariableElement field,
            String expectedType,
            String compositeType,
            TypeBuilder builder,
            BiConsumer<TypeDefinition, TypeDefinition>... assertions) {
        Map<String, TypeDefinition> typeCache = new HashMap<>();
        TypeDefinition typeDefinition = TypeDefinitionBuilder.build(processingEnv, field, typeCache);
        String subTypeName = typeDefinition.getItems().get(0);
        TypeDefinition subTypeDefinition = typeCache.get(subTypeName);
        assertEquals(expectedType, typeDefinition.getType());
        //        assertEquals(field.getSimpleName().toString(), typeDefinition.get$ref());
        assertEquals(compositeType, subTypeDefinition.getType());
        //        assertEquals(builder.getClass().getName(), typeDefinition.getTypeBuilderName());
        Stream.of(assertions).forEach(assertion -> assertion.accept(typeDefinition, subTypeDefinition));
    }
}
