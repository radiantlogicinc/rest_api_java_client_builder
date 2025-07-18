package com.radiantlogic.openapi.codegen.javaclient.generate.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.codegen.CodegenDiscriminator;

/**
 * Extended version of the CodegenDiscriminator to add extra properties to deal with a lack of a
 * mapping in the spec.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ExtendedCodegenDiscriminator extends CodegenDiscriminator {

  public boolean useConstructor() {
    return getPropertyType().equals("BigDecimal");
  }

  public boolean useCast() {
    return !getIsEnum() && !useConstructor();
  }
}
