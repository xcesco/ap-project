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

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountListCommandTest extends BaseCommandTest {
  CountListCommandTest() {
    super(ComputationType.COUNT, ValueType.LIST);
  }

  @Test
  void testValidCommand() throws MalformedCommandException {
    verify("x0:0:.1:1;x0;y0", 11.0);
    verify("x0:1:1:10;x0", 10.0);
    verify("x0:1:1:10,y0:1:1:10;x0", 10.0);
    verify("x0:1:1:10,y0:1:1:10,y2:1:1:10;x0", 10.0);
  }

  @Test
  void testExample() throws MalformedCommandException {
    verify("x0:1:0.001:100;x0", 99001.0);
    verify("x0:0:1:99000;x1", 99001.0);
  }

  @Test
  void testExample90001() throws MalformedCommandException {
    verify("x0:0:1:99000;x1", 99001.0);
  }

  @Test
  void testExampleUnit() throws MalformedCommandException {
    verify("x0:0:0.01:0.1;x1", 11.0);
  }

  @Test
  public void getValuesRangeTest() {
    verify("x1:-1.2:0.1:0;x1", 13.0);
  }


}
