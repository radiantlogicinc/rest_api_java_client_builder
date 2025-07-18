package com.radiantlogic.openapi.codegen.javaclient.generate.codegen.support;

import com.radiantlogic.openapi.codegen.javaclient.generate.codegen.utils.CodegenConstants;
import com.radiantlogic.openapi.codegen.javaclient.generate.codegen.utils.CodegenModelUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;

/**
 * The "Raw" types are automatically added to discriminated union interfaces if they lack the
 * mapping information to correctly serialize/de-serialize to/from the sub types. This is a
 * workaround to compensate for an incomplete OpenAPI specification.
 *
 * <p>This support class updates models and operations so that any usage of a discriminated union
 * lacking its mapping information is adjusted to use the Raw type instead.
 */
public class CodegenRawTypeUsageSupport {
  public void applyRawTypesToModelProperties(
      @NonNull final Map<String, CodegenModel> modelClassMap) {
    modelClassMap.values().stream()
        .flatMap(model -> model.vars.stream())
        .filter(prop -> Objects.nonNull(prop.complexType))
        .filter(prop -> modelClassMap.containsKey(prop.complexType))
        .filter(prop -> CodegenModelUtils.isInvalidUnionType(modelClassMap.get(prop.complexType)))
        .forEach(CodegenRawTypeUsageSupport::convertPropertyToRawType);
  }

  private static void convertPropertyToRawType(@NonNull final CodegenProperty prop) {
    final String complexType = "%s.Raw".formatted(prop.complexType);
    prop.datatypeWithEnum = prop.datatypeWithEnum.replaceAll(prop.complexType, complexType);
    prop.complexType = complexType;
    if (prop.items != null) {
      prop.items.complexType = complexType;
      prop.items.datatypeWithEnum = complexType;
    }
  }

  public void applyRawTypesToOperationTypes(
      @NonNull final List<CodegenOperation> operations,
      @NonNull final Map<String, CodegenModel> allModelsClassMap) {
    // Transform return types to Raw types where necessary
    operations.stream()
        .filter(operation -> operation.returnBaseType != null)
        .filter(operation -> allModelsClassMap.containsKey(operation.returnBaseType))
        .map(operation -> toOperationWithReturnType(operation, allModelsClassMap))
        .filter(opAndType -> CodegenModelUtils.isInvalidUnionType(opAndType.returnType()))
        .forEach(CodegenRawTypeUsageSupport::convertToRawReturnType);

    // Transform all params to Raw types where necessary
    operations.stream()
        .filter(operation -> operation.allParams != null && !operation.allParams.isEmpty())
        .flatMap(operation -> operation.allParams.stream())
        .filter(param -> allModelsClassMap.containsKey(param.baseType))
        .map(param -> toParamWithType(param, allModelsClassMap))
        .filter(paramAndType -> CodegenModelUtils.isInvalidUnionType(paramAndType.type()))
        .forEach(CodegenRawTypeUsageSupport::convertToRawParamType);
  }

  private static void convertToRawParamType(@NonNull final ParamWithType paramAndType) {
    final String bodyParamBaseType = "%s.Raw".formatted(paramAndType.param().baseType);
    paramAndType.param().baseType = bodyParamBaseType;
    if (CodegenConstants.LIST_TYPE_PATTERN.matcher(paramAndType.param().dataType).matches()) {
      paramAndType.param().dataType = "List<%s>".formatted(bodyParamBaseType);
    } else {
      paramAndType.param().dataType = bodyParamBaseType;
    }
  }

  private static void convertToRawReturnType(@NonNull final OperationWithReturnType opAndType) {
    final String returnBaseType = "%s.Raw".formatted(opAndType.operation().returnBaseType);
    opAndType.operation().returnBaseType = returnBaseType;
    if (CodegenConstants.LIST_TYPE_PATTERN.matcher(opAndType.operation().returnType).matches()) {
      opAndType.operation().returnType = "List<%s>".formatted(returnBaseType);
    } else {
      opAndType.operation().returnType = returnBaseType;
    }
  }

  private static OperationWithReturnType toOperationWithReturnType(
      @NonNull final CodegenOperation operation,
      @NonNull final Map<String, CodegenModel> allModelsClassMap) {
    final CodegenModel returnType = allModelsClassMap.get(operation.returnBaseType);
    return new OperationWithReturnType(operation, returnType);
  }

  private static ParamWithType toParamWithType(
      @NonNull final CodegenParameter param,
      @NonNull final Map<String, CodegenModel> allModelsClassMap) {
    final CodegenModel type = allModelsClassMap.get(param.baseType);
    return new ParamWithType(param, type);
  }

  private record OperationWithReturnType(
      @NonNull CodegenOperation operation, @NonNull CodegenModel returnType) {}

  private record ParamWithType(@NonNull CodegenParameter param, @NonNull CodegenModel type) {}
}
