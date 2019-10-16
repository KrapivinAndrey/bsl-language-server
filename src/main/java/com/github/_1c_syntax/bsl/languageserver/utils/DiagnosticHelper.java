/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2019
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
package com.github._1c_syntax.bsl.languageserver.utils;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DiagnosticHelper {

  private DiagnosticHelper() {
    // Utility class
  }

  public static boolean equalNodes(Tree leftNode, Tree rightNode) {

    if (leftNode.getChildCount() != rightNode.getChildCount()
      || !leftNode.getClass().equals(rightNode.getClass())) {
      return false;
    }

    if (leftNode instanceof TerminalNode) {

      int leftNodeType = ((TerminalNode) leftNode).getSymbol().getType();
      int rightNodeType = ((TerminalNode) rightNode).getSymbol().getType();

      if (leftNodeType != rightNodeType
        || (leftNodeType == BSLParser.STRING
        && !leftNode.toString().equals(rightNode.toString()))
        || (!leftNode.toString().equalsIgnoreCase(rightNode.toString()))) {
        return false;
      }

    }

    for (int i = 0; i < leftNode.getChildCount(); i++) {
      if (!equalNodes(leftNode.getChild(i), rightNode.getChild(i))) {
        return false;
      }
    }

    return true;
  }

  public static boolean isStructureType(ParseTree tnc) {
    return "Структура".equalsIgnoreCase(tnc.getText()) || "Structure".equalsIgnoreCase(tnc.getText());
  }

  public static boolean isFixedStructureType(ParseTree tnc) {
    return "ФиксированнаяСтруктура".equalsIgnoreCase(tnc.getText()) || "FixedStructure".equalsIgnoreCase(tnc.getText());
  }

  /**
   * Проверяет среди дочерних элементов наличие ноды с ошибкой
   * @return true - если есть нода с ошибкой
   */
  public static boolean findErrorNode(ParseTree tnc) {

    if (tnc instanceof BSLParserRuleContext) {
      if (((BSLParserRuleContext) tnc).exception != null) {
        return true;
      }

      for (int i = 0; i < tnc.getChildCount(); i++) {
        if (findErrorNode(tnc.getChild(i))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Выполняет поиск предыдущей ноды нужного типа
   * @param parent - родительская нода, среди дочерних которой производится поиск
   * @param tnc - нода, для которой ищем предыдущую
   * @param ruleindex - BSLParser.RULE_*
   * @return tnc - если предыдущая нода не найдена, вернет текущую
   */
  public static ParseTree getPreviousNode(ParseTree parent, ParseTree tnc, int ruleindex) {
    List<ParseTree> statements = new ArrayList<>(Trees.findAllRuleNodes(parent, ruleindex));

    int pos = statements.indexOf(tnc);
    if ((pos - 1) > -1) {
      return statements.get(pos - 1);
    }
    return tnc;
  }

  /**
   * Рекурсивно находит самого верхнего родителя текущей ноды
   */
  public static ParseTree getRootParent(ParseTree tnc) {
    if(tnc.getParent() != null) {
      return getRootParent(tnc.getParent());
    }

    return tnc;
  }
}
