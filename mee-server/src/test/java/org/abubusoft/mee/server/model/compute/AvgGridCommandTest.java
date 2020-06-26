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
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AvgGridCommandTest extends BaseCommandTest {

  public AvgGridCommandTest() {
    super(ComputationType.AVG, ValueType.GRID);
  }

  @Test
  public void tesValidCommand() {
    verify("x0:1:1:10;x0;(x0+2)", 5.5);
    verify("x0:1:1:10,y0:1:1:10;(x0+y0)", 11.0);
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> verify("x0:0:0:10;x0", 5.5));
    verify("x0:1:1:10,x1:1:1:10;(x0+x1)", 11.0);
    verify("x0:1:1:10,x1:1:1:10;(x0+(x1-1))", 10.0);
    verify("x0:1:1:10,x1:1:1:10;(x0+(x1-1));y1", 10.0);
    Assertions.assertThrows(UndefinedVariableException.class, () -> verify("x0:1:1:10,x1:1:1:10;(x0+(y1-1));x1", 10.0));
  }

}
