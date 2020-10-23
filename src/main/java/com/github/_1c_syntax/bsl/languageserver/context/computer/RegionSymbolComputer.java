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
package com.github._1c_syntax.bsl.languageserver.context.computer;

import com.github._1c_syntax.bsl.languageserver.context.BSLDocumentContext;
import com.github._1c_syntax.bsl.languageserver.context.symbol.RegionSymbol;
import com.github._1c_syntax.bsl.languageserver.utils.BSLRanges;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp4j.Range;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class RegionSymbolComputer
  extends BSLParserBaseVisitor<ParseTree>
  implements Computer<List<RegionSymbol>> {

  private final BSLDocumentContext documentContext;
  private final Deque<Pair<RegionSymbol.RegionSymbolBuilder, BSLParser.RegionStartContext>> regionStack =
    new ArrayDeque<>();
  private final List<RegionSymbol> regions = new ArrayList<>();

  public RegionSymbolComputer(BSLDocumentContext documentContext) {
    this.documentContext = documentContext;
  }

  @Override
  public List<RegionSymbol> compute() {
    regionStack.clear();
    regions.clear();

    visitFile(documentContext.getAst());

    regionStack.clear();

    List<RegionSymbol> result = new ArrayList<>(regions);
    regions.clear();

    return result;
  }

  @Override
  public ParseTree visitRegionStart(BSLParser.RegionStartContext ctx) {

    RegionSymbol.RegionSymbolBuilder builder = RegionSymbol.builder()
      .name(ctx.regionName().getText())
      .regionNameRange(BSLRanges.create(ctx.regionName()))
      .startRange(BSLRanges.create(ctx));

    regionStack.push(Pair.of(builder, ctx));
    return super.visitRegionStart(ctx);
  }

  @Override
  public ParseTree visitRegionEnd(BSLParser.RegionEndContext ctx) {

    if (regionStack.isEmpty()) {
      return super.visitRegionEnd(ctx);
    }

    var pair = regionStack.pop();

    RegionSymbol.RegionSymbolBuilder builder = pair.getLeft();
    BSLParser.RegionStartContext regionStartContext = pair.getRight();

    Range range = BSLRanges.create(regionStartContext, ctx);
    builder
      .range(range)
      .endRange(BSLRanges.create(ctx))
    ;

    RegionSymbol region = builder.build();

    regions.add(region);

    return super.visitRegionEnd(ctx);
  }

}
