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
package com.github._1c_syntax.bsl.languageserver.providers;

import com.github._1c_syntax.bsl.languageserver.context.DocumentContext;
import com.github._1c_syntax.bsl.languageserver.context.callee.CalleeStorage;
import com.github._1c_syntax.bsl.languageserver.context.symbol.MethodSymbol;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefinitionProvider {

  private final CalleeStorage calleeStorage;

  public List<LocationLink> getDefinition(DocumentContext documentContext, DefinitionParams params) {
    Position position = params.getPosition();

    return calleeStorage.getCalledMethodSymbol(documentContext.getUri(), position)
      .map((Pair<MethodSymbol, Range> entry) -> {
        MethodSymbol methodSymbol = entry.getKey();

        String targetUri = methodSymbol.getUri().toString();
        Range targetRange = methodSymbol.getRange();
        Range targetSelectionRange = methodSymbol.getSubNameRange();
        Range originSelectionRange = entry.getValue();

        return new LocationLink(
          targetUri,
          targetRange,
          targetSelectionRange,
          originSelectionRange
        );
      })
      .map(List::of)
      .orElse(Collections.emptyList());
  }
}
