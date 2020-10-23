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
package com.github._1c_syntax.bsl.languageserver.util;

import com.github._1c_syntax.bsl.languageserver.context.BSLDocumentContext;
import com.github._1c_syntax.bsl.languageserver.context.BSLServerContext;
import com.github._1c_syntax.utils.Absolute;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.TestComponent;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@TestComponent
public class TestUtils {

  public static final URI FAKE_DOCUMENT_URI = Absolute.uri("file:///fake-uri.bsl");
  public static final String PATH_TO_METADATA = "src/test/resources/metadata";

  @SneakyThrows
  public static BSLDocumentContext getDocumentContextFromFile(String filePath) {

    String fileContent = FileUtils.readFileToString(
      new File(filePath),
      StandardCharsets.UTF_8
    );

    return getDocumentContext(Path.of(filePath).toUri(), fileContent);
  }

  public static BSLDocumentContext getDocumentContext(URI uri, String fileContent) {
    return getDocumentContext(uri, fileContent, TestApplicationContext.getBean(BSLServerContext.class));
  }

  public static BSLDocumentContext getDocumentContext(String fileContent) {
    return getDocumentContext(FAKE_DOCUMENT_URI, fileContent);
  }

  public static BSLDocumentContext getDocumentContext(String fileContent, @Nullable BSLServerContext context) {
    BSLServerContext passedContext = context;
    if (passedContext == null) {
      passedContext = TestApplicationContext.getBean(BSLServerContext.class);
    }

    return getDocumentContext(FAKE_DOCUMENT_URI, fileContent, passedContext);
  }

  public static BSLDocumentContext getDocumentContext(URI uri, String fileContent, BSLServerContext context) {
    return context.addDocument(uri, fileContent);
  }
}
