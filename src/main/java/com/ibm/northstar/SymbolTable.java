package com.ibm.northstar;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.ibm.northstar.entities.*;
import com.ibm.northstar.utils.Log;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("rawtypes")
public class SymbolTable {

    private static JavaSymbolSolver javaSymbolSolver;
    private static Set<String> unresolvedTypes = new HashSet<>();
    private static Set<String> unresolvedExpressions = new HashSet<>();

    /**
     * Processes the given compilation unit to extract information about classes and
     * interfaces
     * declared in the unit and returns a JSON object containing the extracted
     * information.
     *
     * @param parseResult compilation unit to be processed
     * @return JSON object containing extracted information
     */

    // Let's store the known callables here for future use.
    public static Table<String, String, Callable> declaredMethodsAndConstructors = Tables.newCustomTable(new HashMap<>(), () -> new HashMap<>() {
        @Override
        public Callable get(Object key) {
            if (key instanceof String) {
                Optional<Entry<String, Callable>> matchingEntry = this.entrySet().stream().filter(entry -> isMethodSignatureMatch((String) key, entry.getKey())).findFirst();
                if (matchingEntry.isPresent()) {
                    return matchingEntry.get().getValue();
                }
            }
            return super.get(key);
        }

        private boolean isMethodSignatureMatch(String fullSignature, String searchSignature) {
            String methodName = fullSignature.split("\\(")[0];
            String searchMethodName = searchSignature.split("\\(")[0];

            // Check method name match
            if (!methodName.equals(searchMethodName)) {
                return false;
            }

            // Extract parameters, split by comma, and trim
            String[] fullParams = fullSignature.substring(fullSignature.indexOf("(") + 1, fullSignature.lastIndexOf(")")).split(",");
            String[] searchParams = searchSignature.substring(searchSignature.indexOf("(") + 1, searchSignature.lastIndexOf(")")).split(",");

            // Allow matching with fewer search parameters
            if (searchParams.length != fullParams.length) {
                return false;
            }

            return IntStream.range(0, searchParams.length).allMatch(i -> {
                String fullParamTrimmed = fullParams[i].trim();
                String searchParamTrimmed = searchParams[i].trim();
                return fullParamTrimmed.endsWith(searchParamTrimmed);
            });
        }
    });

    private static JavaCompilationUnit processCompilationUnit(CompilationUnit parseResult) {
        JavaCompilationUnit cUnit = new JavaCompilationUnit();

        cUnit.setFilePath(parseResult.getStorage().map(s -> s.getPath().toString()).orElse("<in-memory>"));

        // Add the comment field to the compilation unit
        cUnit.setComment(parseResult.getComment().isPresent() ? parseResult.getComment().get().asString() : "");

        // Add imports
        cUnit.setImports(parseResult.getImports().stream().map(NodeWithName::getNameAsString).collect(Collectors.toList()));

        // create array node for type declarations
        cUnit.setTypeDeclarations(parseResult.findAll(TypeDeclaration.class)
            .stream().filter(typeDecl -> typeDecl.getFullyQualifiedName().isPresent())
            .map(typeDecl -> {
                // get type name and initialize the type object
                String typeName = typeDecl.getFullyQualifiedName().get().toString();
                com.ibm.northstar.entities.Type typeNode = new com.ibm.northstar.entities.Type();

                if (typeDecl instanceof ClassOrInterfaceDeclaration) {
                    ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration)typeDecl;

                    // Add interfaces implemented by class
                    typeNode.setImplementsList(classDecl.getImplementedTypes().stream().map(SymbolTable::resolveType)
                        .collect(Collectors.toList()));

                    // Add class modifiers
                    typeNode.setModifiers(classDecl.getModifiers().stream().map(m -> m.toString().strip())
                        .collect(Collectors.toList()));

                    // Add class annotations
                    typeNode.setAnnotations(classDecl.getAnnotations().stream().map(a -> a.toString().strip())
                        .collect(Collectors.toList()));

                    // add booleans indicating interfaces and inner/local classes
                    typeNode.setInterface(classDecl.isInterface());
                    typeNode.setInnerClass(classDecl.isInnerClass());
                    typeNode.setLocalClass(classDecl.isLocalClassDeclaration());

                    // Add extends
                    typeNode.setExtendsList(classDecl.getExtendedTypes().stream()
                        .map(SymbolTable::resolveType)
                        .collect(Collectors.toList()));

                } else if (typeDecl instanceof EnumDeclaration) {
                    EnumDeclaration enumDecl = (EnumDeclaration)typeDecl;

                    // Add interfaces implemented by enum
                    typeNode.setImplementsList(enumDecl.getImplementedTypes().stream().map(SymbolTable::resolveType)
                        .collect(Collectors.toList()));

                    // Add enum modifiers
                    typeNode.setModifiers(enumDecl.getModifiers().stream().map(m -> m.toString().strip())
                        .collect(Collectors.toList()));

                    // Add enum annotations
                    typeNode.setAnnotations(enumDecl.getAnnotations().stream().map(a -> a.toString().strip())
                        .collect(Collectors.toList()));

                    // Add enum constants
                    typeNode.setEnumConstants(enumDecl.getEntries().stream()
                        .map(SymbolTable::processEnumConstantDeclaration).collect(Collectors.toList()));

                } else {
                    // TODO: handle AnnotationDeclaration, RecordDeclaration
                    // set the common type attributes only
                    Log.warn("Found unsupported type declaration: "+typeDecl.toString());
                    typeNode = new com.ibm.northstar.entities.Type();
                }

                /* set common attributes of types that available in type declarations:
                is nested type, is class or interface declaration, is enum declaration,
                comments, parent class, callable declarations, field declarations */

                // Set fields indicating nested, class/interface, enum, annotation, and record types
                typeNode.setNestedType(typeDecl.isNestedType());
                typeNode.setClassOrInterfaceDeclaration(typeDecl.isClassOrInterfaceDeclaration());
                typeNode.setEnumDeclaration(typeDecl.isEnumDeclaration());
                typeNode.setAnnotationDeclaration(typeDecl.isAnnotationDeclaration());
                typeNode.setRecordDeclaration(typeDecl.isRecordDeclaration());

                // Add class comment
                typeNode.setComment(typeDecl.getComment().isPresent() ? typeDecl.getComment().get().asString() : "");

                // add parent class (for nested type declarations)
                typeNode.setParentType(typeDecl.getParentNode().get() instanceof TypeDeclaration ?
                    ((TypeDeclaration<TypeDeclaration<?>>)typeDecl.getParentNode().get()).getFullyQualifiedName().get() : "");

                typeNode.setNestedTypeDeclarations(typeDecl.findAll(TypeDeclaration.class).stream()
                    .filter(typ -> typ.isClassOrInterfaceDeclaration() || typ.isEnumDeclaration())
                    .filter(typ -> typ.getParentNode().isPresent() && typ.getParentNode().get() == typeDecl)
                    .map(typ -> typ.getFullyQualifiedName().get().toString()).collect(Collectors.toList()));

                // Add information about declared fields (filtering to fields declared in the
                // type, not in a nested type)
                typeNode.setFieldDeclarations(typeDecl.findAll(FieldDeclaration.class).stream()
                    .filter(f -> f.getParentNode().isPresent() && f.getParentNode().get() == typeDecl)
                    .map(SymbolTable::processFieldDeclaration).collect(Collectors.toList()));
                List<String> fieldNames = new ArrayList<>();
                typeNode.getFieldDeclarations().stream().map(fd -> fd.getVariables()).forEach(fieldNames::addAll);

                // Add information about declared methods (filtering to methods declared in the class, not in a nested class)
                typeNode.setCallableDeclarations(typeDecl.findAll(CallableDeclaration.class).stream()
                    .filter(c -> c.getParentNode().isPresent() && c.getParentNode().get() == typeDecl)
                    .map(meth -> {
                        Pair<String, Callable> callableDeclaration = processCallableDeclaration(meth, fieldNames, typeName);
                        declaredMethodsAndConstructors.put(typeName, callableDeclaration.getLeft(), callableDeclaration.getRight());
                        return callableDeclaration;
                    }).collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight())));

            return Pair.of(typeName, typeNode);
        }).collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight())));

        return cUnit;
    }

    /**
     * Process enum constant declaration.
     *
     * @param enumConstDecl enum constant declaration to be processed
     * @return EnumConstant object containing extracted information
     */
    private static EnumConstant processEnumConstantDeclaration(EnumConstantDeclaration enumConstDecl) {
        EnumConstant enumConstant = new EnumConstant();

        // add enum constant name
        enumConstant.setName(enumConstDecl.getNameAsString());

        // add enum constant arguments
        enumConstant.setArguments(enumConstDecl.getArguments().stream().map(a -> a.toString())
            .collect(Collectors.toList()));

        return enumConstant;
    }

    /**
     * Process parameter declarations on callables.
     *
     * @param paramDecl parameter declaration to be processed
     */
    private static ParameterInCallable processParameterDeclaration(Parameter paramDecl) {
        ParameterInCallable parameter = new ParameterInCallable();

        parameter.setType(resolveType(paramDecl.getType()));
        parameter.setName(paramDecl.getName().toString());
        parameter.setAnnotations(paramDecl.getAnnotations().stream().map(a -> a.toString().strip()).collect(Collectors.toList()));
        parameter.setModifiers(paramDecl.getModifiers().stream().map(a -> a.toString().strip()).collect(Collectors.toList()));
        return parameter;
    }

    /**
     * Processes the given callable declaration to extract information about the
     * declared method or
     * constructor and returns a JSON object containing the extracted information.
     *
     * @param callableDecl callable (method or constructor) to be processed
     * @return Callable object containing extracted information
     */
    @SuppressWarnings("unchecked")
    private static Pair<String, Callable> processCallableDeclaration(CallableDeclaration callableDecl,
                                                                     List<String> classFields, String typeName) {
        Callable callableNode = new Callable();

        // add callable signature
        callableNode.setSignature(callableDecl.getSignature().asString());

        // add comment associated with method/constructor
        callableNode.setComment(callableDecl.getComment().isPresent() ? callableDecl.getComment().get().asString() : "");

        // add annotations on method/constructor
        callableNode.setAnnotations((List<String>) callableDecl.getAnnotations().stream().map(mod -> mod.toString().strip()).collect(Collectors.toList()));

        // add method or constructor modifiers
        callableNode.setModifiers((List<String>) callableDecl.getModifiers().stream().map(mod -> mod.toString().strip()).collect(Collectors.toList()));

        // add exceptions declared in "throws" clause
        callableNode.setThrownExceptions(
            ((NodeList<ReferenceType>)callableDecl.getThrownExceptions())
            .stream()
            .map(SymbolTable::resolveType)
            .collect(Collectors.toList()));

        // add the complete declaration string, including modifiers, throws, and
        // parameter names
        callableNode.setDeclaration(callableDecl.getDeclarationAsString(true, true, true).strip());

        // add information about callable parameters: for each parameter, type, name,
        // annotations,
        // modifiers
        callableNode.setParameters((List<ParameterInCallable>) callableDecl.getParameters().stream().map(param -> processParameterDeclaration((Parameter) param)).collect(Collectors.toList()));

        // A method declaration may not have a body if it is an abstract method. A
        // constructor always
        // has a body. So, we need to check if the body is present before processing it
        // and capture it
        // using the Optional type.
        Optional<BlockStmt> body = (callableDecl instanceof MethodDeclaration) ? ((MethodDeclaration) callableDecl).getBody() : Optional.ofNullable(((ConstructorDeclaration) callableDecl).getBody());

        // Same as above, a constructor declaration may not have a return type
        // and method declaration always has a return type.
        callableNode.setReturnType((callableDecl instanceof MethodDeclaration) ?
            resolveType(((MethodDeclaration)callableDecl).getType()) : null);

        callableNode.setConstructor(callableDecl instanceof ConstructorDeclaration);
        callableNode.setStartLine(callableDecl.getRange().isPresent() ? callableDecl.getRange().get().begin.line : -1);
        callableNode.setEndLine(callableDecl.getRange().isPresent() ? callableDecl.getRange().get().end.line : -1);
        callableNode.setReferencedTypes(getReferencedTypes(body));
        callableNode.setCode(body.isPresent() ? body.get().toString() : "");

        callableNode.setAccessedFields(getAccessedFields(body, classFields, typeName));
        callableNode.setCallSites(getCallSites(body));
        callableNode.setVariableDeclarations(getVariableDeclarations(body));

        String callableSignature = (callableDecl instanceof MethodDeclaration) ? callableDecl.getSignature().asString() : callableDecl.getSignature().asString().replace(callableDecl.getSignature().getName(), "<init>");
        return Pair.of(callableSignature, callableNode);
    }

    /**
     * Processes the given field declaration to extract information about the
     * declared field and
     * returns a JSON object containing the extracted information.
     *
     * @param fieldDecl field declaration to be processed
     * @return Field object containing extracted information
     */
    private static Field processFieldDeclaration(FieldDeclaration fieldDecl) {
        Field field = new Field();

        // add comment associated with field
        field.setComment(fieldDecl.getComment().isPresent() ? fieldDecl.getComment().get().asString() : "");

        // add annotations on field
        field.setAnnotations(fieldDecl.getAnnotations().stream().map(a -> a.toString().strip()).collect(Collectors.toList()));

        // add variable names
        field.setVariables(fieldDecl.getVariables().stream().map(v -> v.getName().asString()).collect(Collectors.toList()));

        // add field modifiers
        field.setModifiers(fieldDecl.getModifiers().stream().map(m -> m.toString().strip()).collect(Collectors.toList()));

        // add field type
        field.setType(resolveType(fieldDecl.getCommonType()));

        // add field start and end lines
        field.setStartLine(fieldDecl.getRange().isPresent() ? fieldDecl.getRange().get().begin.line : null);

        field.setEndLine(fieldDecl.getRange().get().end.line);

        return field;
    }

    /**
     * Computes and returns the set of types references in a block of statement
     * (method or constructor
     * body).
     *
     * @param blockStmt Block statement to compute referenced types for
     * @return List of types referenced in the block statement
     */
    private static List<String> getReferencedTypes(Optional<BlockStmt> blockStmt) {
        Set<String> referencedTypes = new HashSet<>();
        blockStmt.ifPresent(bs -> bs.findAll(VariableDeclarator.class)
            .stream()
            .filter(vd -> vd.getType().isClassOrInterfaceType())
            .map(vd -> resolveType(vd.getType()))
            .forEach(referencedTypes::add));

        // add types of accessed fields to the set of referenced types
        blockStmt.ifPresent(bs -> bs.findAll(FieldAccessExpr.class)
            .stream()
            .filter(faExpr -> faExpr.getParentNode().isPresent() && !(faExpr.getParentNode().get() instanceof FieldAccessExpr))
            .map(faExpr -> {
                if (faExpr.getParentNode().isPresent() && faExpr.getParentNode().get() instanceof CastExpr) {
                    return resolveType(((CastExpr)faExpr.getParentNode().get()).getType());
                } else {
                    return resolveExpression(faExpr);
                }
            })
            .filter(type -> !type.isEmpty())
            .forEach(referencedTypes::add));

        // TODO: add resolved method access expressions

        return new ArrayList<>(referencedTypes);
    }

    /**
     * Returns information about variable declarations in the given callable. The information includes
     * var name, var type, var initializer, and position.
     *
     * @param blockStmt Callable to compute var declaration information for
     * @return list of variable declarations
     */
    private static List<VariableDeclaration> getVariableDeclarations(Optional<BlockStmt> blockStmt) {
        List<VariableDeclaration> varDeclarations = new ArrayList<>();
        if (blockStmt.isEmpty()) {
            return varDeclarations;
        }
        for (VariableDeclarator declarator : blockStmt.get().findAll(VariableDeclarator.class)) {
            VariableDeclaration varDeclaration = new VariableDeclaration();
            varDeclaration.setName(declarator.getNameAsString());
            varDeclaration.setType(resolveType(declarator.getType()));
            varDeclaration.setInitializer(declarator.getInitializer().isPresent() ?
                declarator.getInitializer().get().toString() : "");
            if (declarator.getRange().isPresent()) {
                varDeclaration.setStartLine(declarator.getRange().get().begin.line);
                varDeclaration.setStartColumn(declarator.getRange().get().begin.column);
                varDeclaration.setEndLine(declarator.getRange().get().end.line);
                varDeclaration.setEndColumn(declarator.getRange().get().end.column);
            } else {
                varDeclaration.setStartLine(-1);
                varDeclaration.setStartColumn(-1);
                varDeclaration.setEndLine(-1);
                varDeclaration.setEndColumn(-1);
            }
            varDeclarations.add(varDeclaration);
        }
        return varDeclarations;
    }

    /**
     * Computes and returns the list of fields accessed in the given callable body. The returned values contain
     * field names qualified by names of the declaring types.
     *
     * @param callableBody Callable body to compute accessed fields for
     * @return List of fully qualified field names
     */
    private static List<String> getAccessedFields(Optional<BlockStmt> callableBody, List<String> classFields,
                                                  String typeName) {
        Set<String> accessedFields = new HashSet<>();

        // process field access expressions in the callable
        callableBody.ifPresent(cb -> cb.findAll(FieldAccessExpr.class)
            .stream()
            .filter(faExpr -> faExpr.getParentNode().isPresent() && !(faExpr.getParentNode().get() instanceof FieldAccessExpr))
            .map(faExpr -> {
                String fieldDeclaringType = resolveExpression(faExpr.getScope());
                if (!fieldDeclaringType.isEmpty()) {
                    return fieldDeclaringType + "." + faExpr.getNameAsString();
                } else {
                    return faExpr.getNameAsString();
                }
            })
            .forEach(accessedFields::add)
        );

        // process all names expressions in callable and match against names of declared fields
        // in class TODO: handle local variable declarations with the same name
        if (callableBody.isPresent()) {
            for (NameExpr nameExpr : callableBody.get().findAll(NameExpr.class)) {
                for (String fieldName : classFields) {
                    if (nameExpr.getNameAsString().equals(fieldName)) {
                        accessedFields.add(typeName + "." + nameExpr.getNameAsString());
                    }
                }
            }
        }

        return new ArrayList<>(accessedFields);
    }

    /**
     * Returns information about call sites in the given callable. The information includes:
     * the method name, the declaring type name, and types of arguments used in method call.
     *
     * @param callableBody callable to compute call-site information for
     * @return list of call sites
     */
    private static List<CallSite> getCallSites(Optional<BlockStmt> callableBody) {
        List<CallSite> callSites = new ArrayList<>();
        if (callableBody.isEmpty()) {
            return callSites;
        }
        for (MethodCallExpr methodCallExpr : callableBody.get().findAll(MethodCallExpr.class)) {
            // resolve declaring type for called method
            boolean isStaticCall = false;
            String declaringType = "";
            String receiverName = "";
            String returnType = "";
            if (methodCallExpr.getScope().isPresent()) {
                Expression scopeExpr = methodCallExpr.getScope().get();
                receiverName = scopeExpr.toString();
                declaringType = resolveExpression(scopeExpr);
                if (declaringType.contains(" | ")) {
                    declaringType = declaringType.split(" \\| ")[0];
                }
                String declaringTypeName = declaringType.contains(".") ?
                    declaringType.substring(declaringType.lastIndexOf(".")+1) : declaringType;
                if (declaringTypeName.equals(scopeExpr.toString())) {
                    isStaticCall = true;
                }
            }

            // compute return type for method call taking into account typecast of return value
            if (methodCallExpr.getParentNode().isPresent() && methodCallExpr.getParentNode().get() instanceof CastExpr) {
                returnType = resolveType(((CastExpr)methodCallExpr.getParentNode().get()).getType());
            } else {
                returnType = resolveExpression(methodCallExpr);
            }

            // resolve callee and get signature
            String calleeSignature = "";
            try {
                calleeSignature = methodCallExpr.resolve().getSignature();
            } catch (RuntimeException exception) {
                Log.debug("Could not resolve method call: " + methodCallExpr + ": " + exception.getMessage());
            }

            // Resolve access qualifier
            AccessSpecifier accessSpecifier = AccessSpecifier.NONE;
            try {
                ResolvedMethodDeclaration resolvedMethodDeclaration = methodCallExpr.resolve();
                accessSpecifier = resolvedMethodDeclaration.accessSpecifier();
            }
            catch (RuntimeException exception) {
                Log.debug("Could not resolve access specifier for method call: " + methodCallExpr + ": " + exception.getMessage());
            }
            // resolve arguments of the method call to types
            List<String> arguments = methodCallExpr.getArguments().stream()
                .map(SymbolTable::resolveExpression).collect(Collectors.toList());
            // add a new call site object
            callSites.add(createCallSite(methodCallExpr, methodCallExpr.getNameAsString(), receiverName, declaringType,
                arguments, returnType, calleeSignature, isStaticCall, false, accessSpecifier));
        }

        for (ObjectCreationExpr objectCreationExpr : callableBody.get().findAll(ObjectCreationExpr.class)) {
            // resolve declaring type for called method
            String instantiatedType = resolveType(objectCreationExpr.getType());

            // resolve arguments of the constructor call to types
            List<String> arguments = objectCreationExpr.getArguments().stream()
                .map(SymbolTable::resolveExpression).collect(Collectors.toList());

            // resolve callee and get signature
            String calleeSignature = "";
            try {
                calleeSignature = objectCreationExpr.resolve().getSignature();
            } catch (RuntimeException exception) {
                Log.debug("Could not resolve constructor call: " + objectCreationExpr + ": " + exception.getMessage());
            }

            // add a new call site object
            callSites.add(createCallSite(objectCreationExpr, "<init>",
                objectCreationExpr.getScope().isPresent() ? objectCreationExpr.getScope().get().toString() : "",
                instantiatedType, arguments, instantiatedType, calleeSignature,false, true, AccessSpecifier.NONE));
        }

        return callSites;
    }

    /**
     * Creates and returns a new CallSite object for the given expression, which can be a method-call or
     * object-creation expression.
     *
     * @param callExpr
     * @param calleeName
     * @param  receiverExpr
     * @param receiverType
     * @param arguments
     * @param isStaticCall
     * @param isConstructorCall
     * @return
     */
    private static CallSite createCallSite(Expression callExpr, String calleeName, String receiverExpr,
                                           String receiverType, List<String> arguments, String returnType,
                                           String calleeSignature, boolean isStaticCall, boolean isConstructorCall, AccessSpecifier accessSpecifier) {
        CallSite callSite = new CallSite();
        callSite.setMethodName(calleeName);
        callSite.setReceiverExpr(receiverExpr);
        callSite.setReceiverType(receiverType);
        callSite.setArgumentTypes(arguments);
        callSite.setReturnType(returnType);
        callSite.setCalleeSignature(calleeSignature);
        callSite.setStaticCall(isStaticCall);
        callSite.setConstructorCall(isConstructorCall);
        callSite.setPrivate(accessSpecifier.equals(AccessSpecifier.PRIVATE));
        callSite.setPublic(accessSpecifier.equals(AccessSpecifier.PUBLIC));
        callSite.setProtected(accessSpecifier.equals(AccessSpecifier.PROTECTED));
        callSite.setUnspecified(accessSpecifier.equals(AccessSpecifier.NONE));
        if (callExpr.getRange().isPresent()) {
            callSite.setStartLine(callExpr.getRange().get().begin.line);
            callSite.setStartColumn(callExpr.getRange().get().begin.column);
            callSite.setEndLine(callExpr.getRange().get().end.line);
            callSite.setEndColumn(callExpr.getRange().get().end.column);
        } else {
            callSite.setStartLine(-1);
            callSite.setStartColumn(-1);
            callSite.setEndLine(-1);
            callSite.setEndColumn(-1);
        }
        return callSite;
    }

    /**
     * Calculates type for the given expression and returns the resolved type name, or empty string if
     * exception occurs during type resolution.
     *
     * @param expression Expression to be resolved
     * @return Resolved type name or empty string if type resolution fails
     */
    private static String resolveExpression(Expression expression) {
        // perform expression resolution if resolution of this expression did not fail previously
        if (!unresolvedExpressions.contains(expression.toString())) {
            try {
                ResolvedType resolvedType = javaSymbolSolver.calculateType(expression);
                if (resolvedType.isReferenceType() || resolvedType.isUnionType()) {
                    return resolvedType.describe();
                }
            } catch (RuntimeException exception) {
                Log.debug("Could not resolve expression: " + expression + ": " + exception.getMessage());
                unresolvedExpressions.add(expression.toString());
            }
        }
        return "";
    }

    /**
     * Resolves the given type and returns string representation of the resolved type. If type resolution
     * fails, returns string representation (name) of the type.
     * @param type Type to be resolved
     * @return Resolved (qualified) type name
     */
    private static String resolveType(Type type) {
        // perform type resolution if resolution of this type did not fail previously
        if (!unresolvedTypes.contains(type.asString())) {
            try {
                return type.resolve().describe();
            } catch (RuntimeException e) {
                Log.warn("Could not resolve type: " + type.asString() + ": " + e.getMessage());
                unresolvedTypes.add(type.asString());
            }
        }
        return type.asString();
    }

    /**
     * Collects all source roots (e.g., "src/main/java", "src/test/java") under the given project root path
     * using the symbol solver collection strategy. Parses all source files under each source root and
     * returns the complete symbol table as map of file path and java compilation unit pairs.
     *
     * @param projectRootPath root path of the project to be analyzed
     * @return Pair of extracted symbol table map and parse problems map for project
     * @throws IOException
     */
    public static Pair<Map<String, JavaCompilationUnit>, Map<String, List<Problem>>> extractAll(Path projectRootPath) throws IOException {
        SymbolSolverCollectionStrategy symbolSolverCollectionStrategy = new SymbolSolverCollectionStrategy();
        ProjectRoot projectRoot = symbolSolverCollectionStrategy.collect(projectRootPath);
        javaSymbolSolver = (JavaSymbolSolver)symbolSolverCollectionStrategy.getParserConfiguration().getSymbolResolver().get();
        Map<String, JavaCompilationUnit> symbolTable = new LinkedHashMap<>();
        Map<String, List<Problem>> parseProblems = new HashMap<>();
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            for (ParseResult<CompilationUnit> parseResult : sourceRoot.tryToParse()) {
                if (parseResult.isSuccessful()) {
                    CompilationUnit compilationUnit = parseResult.getResult().get();
                    symbolTable.put(
                        compilationUnit.getStorage().get().getPath().toString(),
                        processCompilationUnit(compilationUnit)
                    );
                } else {
                    parseProblems.put(sourceRoot.getRoot().toString(), parseResult.getProblems());
                }
            }
        }
        return Pair.of(symbolTable, parseProblems);
    }

    public static Pair<Map<String, JavaCompilationUnit>, Map<String, List<Problem>>> extractSingle(String code) throws IOException {
        Map symbolTable = new LinkedHashMap<String, JavaCompilationUnit>();
        Map parseProblems = new HashMap<String, List<Problem>>();
        // Setting up symbol solvers
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        JavaParser javaParser = new JavaParser(parserConfiguration);
        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        if (parseResult.isSuccessful()) {
            CompilationUnit compilationUnit = parseResult.getResult().get();
            Log.debug("Successfully parsed code. Now processing compilation unit");
            symbolTable.put("<pseudo-path>", processCompilationUnit(compilationUnit));
        } else {
            Log.error(parseResult.getProblems().toString());
            parseProblems.put("code", parseResult.getProblems());
        }
        return Pair.of(symbolTable, parseProblems);
    }

    /**
     * Parses the given set of Java source files from the given project and constructs the symbol table.
     * @param projectRootPath
     * @param javaFilePaths
     * @return
     * @throws IOException
     */
    public static Pair<Map<String, JavaCompilationUnit>, Map<String, List<Problem>>> extract(
            Path projectRootPath,
            List<Path> javaFilePaths
    ) throws IOException {

        // create symbol solver and parser configuration
        SymbolSolverCollectionStrategy symbolSolverCollectionStrategy = new SymbolSolverCollectionStrategy();
        ProjectRoot projectRoot = symbolSolverCollectionStrategy.collect(projectRootPath);
        javaSymbolSolver = (JavaSymbolSolver)symbolSolverCollectionStrategy.getParserConfiguration().getSymbolResolver().get();
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(javaSymbolSolver);

        // create java parser with the configuration
        JavaParser javaParser = new JavaParser(parserConfiguration);

        Map symbolTable = new LinkedHashMap<String, JavaCompilationUnit>();
        Map parseProblems = new HashMap<String, List<Problem>>();

        // parse all given files and return pair of symbol table and parse problems
        for (Path javaFilePath : javaFilePaths) {
            ParseResult<CompilationUnit> parseResult = javaParser.parse(javaFilePath);
            if (parseResult.isSuccessful()) {
                CompilationUnit compilationUnit = parseResult.getResult().get();
                symbolTable.put(compilationUnit.getStorage().get().getPath().toString(),
                        processCompilationUnit(compilationUnit));
            } else {
                Log.error(parseResult.getProblems().toString());
                parseProblems.put(javaFilePath.toString(), parseResult.getProblems());
            }
        }
        return Pair.of(symbolTable, parseProblems);
    }

    public static void main(String[] args) throws IOException {
        extractAll(Paths.get(args[0]));
    }

}
