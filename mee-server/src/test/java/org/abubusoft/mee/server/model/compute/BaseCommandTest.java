/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.ResponseType;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.junit.jupiter.api.Assertions;

public abstract class BaseCommandTest {
  public BaseCommandTest(ComputationType operation, ValueType valueType) {
    this.operation = operation;
    this.valueKind = valueType;
  }

  private final ClientRequestParser parser = new ClientRequestParserImpl();
  private final ComputationType operation;
  private final ValueType valueKind;

  public String prependType(String input) {
    return operation + "_" + valueKind + ";" + input;
  }

  protected void verify(String expression, double value) {
    ComputeCommand command = (ComputeCommand)parser.parse(prependType(expression));
    CommandResponse response = command.execute();

    Assertions.assertEquals(ResponseType.OK, response.getResponseType());
    Assertions.assertEquals(value, response.getValue());
  }
}
