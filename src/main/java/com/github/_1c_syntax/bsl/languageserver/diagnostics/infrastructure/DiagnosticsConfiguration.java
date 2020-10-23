/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package com.github._1c_syntax.bsl.languageserver.diagnostics.infrastructure;

import com.github._1c_syntax.bsl.languageserver.configuration.BSLLanguageServerConfiguration;
import com.github._1c_syntax.bsl.languageserver.configuration.diagnostics.BSLDiagnosticsOptions;
import com.github._1c_syntax.bsl.languageserver.configuration.diagnostics.SkipSupport;
import com.github._1c_syntax.bsl.languageserver.context.BSLDocumentContext;
import com.github._1c_syntax.bsl.languageserver.context.FileType;
import com.github._1c_syntax.bsl.languageserver.diagnostics.BSLDiagnostic;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticCompatibilityMode;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticInfo;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticScope;
import com.github._1c_syntax.mdclasses.metadata.SupportConfiguration;
import com.github._1c_syntax.mdclasses.metadata.additional.CompatibilityMode;
import com.github._1c_syntax.mdclasses.metadata.additional.ModuleType;
import com.github._1c_syntax.mdclasses.metadata.additional.SupportVariant;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public abstract class DiagnosticsConfiguration {

  private final BSLLanguageServerConfiguration configuration;
  private final DiagnosticConfiguration diagnosticConfiguration;

  @Bean
  @Scope("prototype")
  public List<BSLDiagnostic> diagnostics(BSLDocumentContext documentContext) {

    Collection<DiagnosticInfo> diagnosticInfos = diagnosticInfos();

    BSLDiagnosticsOptions diagnosticsOptions = configuration.getDiagnosticsOptions();

    if (needToComputeDiagnostics(documentContext, diagnosticsOptions)) {
      FileType fileType = documentContext.getFileType();
      CompatibilityMode compatibilityMode = documentContext
        .getServerContext()
        .getConfiguration()
        .getCompatibilityMode();
      ModuleType moduleType = documentContext.getModuleType();

      return diagnosticInfos.stream()
        .filter(diagnosticInfo -> isEnabled(diagnosticInfo, diagnosticsOptions))
        .filter(info -> inScope(info, fileType))
        .filter(info -> correctModuleType(info, moduleType, fileType))
        .filter(info -> passedCompatibilityMode(info, compatibilityMode))
        .map(DiagnosticInfo::getDiagnosticClass)
        .map(diagnosticConfiguration::diagnostic)
        .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  @Lookup("diagnosticInfos")
  protected abstract Collection<DiagnosticInfo> diagnosticInfos();

  private static boolean needToComputeDiagnostics(
    BSLDocumentContext documentContext,
    BSLDiagnosticsOptions diagnosticsOptions
  ) {
    var configuredMode = diagnosticsOptions.getMode();

    if (configuredMode == Mode.OFF) {
      return false;
    }

    var configuredSkipSupport = diagnosticsOptions.getSkipSupport();

    if (configuredSkipSupport == SkipSupport.NEVER) {
      return true;
    }

    Map<SupportConfiguration, SupportVariant> supportVariants = documentContext.getSupportVariants();
    var moduleSupportVariant = supportVariants.values().stream()
      .min(Comparator.naturalOrder())
      .orElse(SupportVariant.NONE);

    if (moduleSupportVariant == SupportVariant.NONE) {
      return true;
    }

    if (configuredSkipSupport == SkipSupport.WITH_SUPPORT_LOCKED) {
      return moduleSupportVariant != SupportVariant.NOT_EDITABLE;
    }

    return configuredSkipSupport != SkipSupport.WITH_SUPPORT;
  }

  private boolean isEnabled(DiagnosticInfo diagnosticInfo, BSLDiagnosticsOptions diagnosticsOptions) {

    var mode = diagnosticsOptions.getMode();
    if (mode == Mode.OFF) {
      return false;
    }

    Either<Boolean, Map<String, Object>> diagnosticParameters =
      configuration.getDiagnosticsOptions().getParameters().get(diagnosticInfo.getCode().getStringValue());

    boolean activatedByDefault = diagnosticParameters == null && diagnosticInfo.isActivatedByDefault();
    boolean hasCustomConfiguration = diagnosticParameters != null && diagnosticParameters.isRight();
    boolean enabledDirectly = diagnosticParameters != null
      && diagnosticParameters.isLeft()
      && diagnosticParameters.getLeft();
    boolean disabledDirectly = diagnosticParameters != null
      && diagnosticParameters.isLeft()
      && !diagnosticParameters.getLeft();
    boolean hasDefinedSetting = enabledDirectly || hasCustomConfiguration;

    boolean passedAllMode = mode == Mode.ALL;
    boolean passedOnlyMode = mode == Mode.ONLY && hasDefinedSetting;
    boolean passedExcept = mode == Mode.EXCEPT && !(hasDefinedSetting || disabledDirectly);
    boolean passedOn = mode == Mode.ON && (activatedByDefault || hasDefinedSetting);

    return passedOn
      || passedAllMode
      || passedOnlyMode
      || passedExcept
      ;

  }

  private static boolean inScope(DiagnosticInfo diagnosticInfo, FileType fileType) {
    DiagnosticScope scope = diagnosticInfo.getScope();
    DiagnosticScope fileScope;
    if (fileType == FileType.OS) {
      fileScope = DiagnosticScope.OS;
    } else {
      fileScope = DiagnosticScope.BSL;
    }
    return scope == DiagnosticScope.ALL || scope == fileScope;
  }

  private static boolean correctModuleType(DiagnosticInfo diagnosticInfo, ModuleType moduletype, FileType fileType) {

    if (fileType == FileType.OS) {
      return true;
    }

    ModuleType[] diagnosticModules = diagnosticInfo.getModules();

    if (diagnosticModules.length == 0) {
      return true;
    }

    boolean contain = false;
    for (ModuleType module : diagnosticModules) {
      if (module == moduletype) {
        contain = true;
        break;
      }
    }
    return contain;
  }

  private static boolean passedCompatibilityMode(
    DiagnosticInfo diagnosticInfo,
    CompatibilityMode contextCompatibilityMode
  ) {
    DiagnosticCompatibilityMode compatibilityMode = diagnosticInfo.getCompatibilityMode();

    if (compatibilityMode == DiagnosticCompatibilityMode.UNDEFINED) {
      return true;
    }

    return CompatibilityMode.compareTo(compatibilityMode.getCompatibilityMode(), contextCompatibilityMode) >= 0;
  }

}
