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

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CountGridCommandTest extends BaseCommandTest {
  CountGridCommandTest() {
    super(ComputationType.COUNT, ValueType.GRID);
  }

  @Test
  void testVariableDefinedTwice() {
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> verify("x0:1:1:10,x0:1:1:10,y2:1:1:10;x0", 1_000.0));
  }

  @Test
  void tesValidCommand() {
    verify("x0:0:.1:1;x0;y0", 11.0);
    verify("x0:0:.1:1;y0;x0", 11.0);
    verify("x0:1:1:10;x0", 10.0);
    verify("x0:0:.1:1;x0", 11.0);
    verify("x0:0:.1:1;x0;x0", 11.0);
    //Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> );
    verify("x0:1:1:10,y0:1:1:10;x0", 100.0);
    verify("x0:0:1:10,y0:1:1:10;x0", 110.0);
    verify("x0:1:1:10,y0:1:1:10,y2:1:1:10;x0", 1_000.0);
  }
}
