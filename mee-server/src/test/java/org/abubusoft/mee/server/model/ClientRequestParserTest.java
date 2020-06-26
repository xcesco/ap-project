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

package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.ComputationType;
import org.abubusoft.mee.server.model.compute.ValueType;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientRequestParserTest {

  ClientRequestParser parser = new ClientRequestParserImpl();

  @Test
  public void testBye() {
    Command command = parser.parse("BYE");
    assertEquals(CommandType.BYE, command.getType());
  }

  @Test
  public void testStatMaxTime() {
    Command command = parser.parse("STAT_MAX_TIME");
    assertEquals(CommandType.STAT, command.getType());
  }

  @Test
  public void testWrongCommand() {
    Assertions.assertThrows(MalformedCommandException.class, () -> {
      parser.parse("QUIT");
    });
  }

  @Test
  public void testMaxGrid() {
    Command command = parser.parse("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(CommandType.COMPUTE, command.getType());
  }

  @Test
  public void testAvgGrid() {
    Command command = parser.parse("AVG_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(CommandType.COMPUTE, command.getType());
  }

  @Test
  public void testMinGrid() {
    Command command = parser.parse("MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(CommandType.COMPUTE, command.getType());
  }

  @Test
  public void testCountList() {
    {
      Command command = parser.parse("COUNT_LIST;x0:.0:0.001:100;x1");
      assertEquals(CommandType.COMPUTE, command.getType());
      CommandResponse response = command.execute();
      assertEquals("OK;0.000;100001.000000", CommandResponseUtils.format(response));
    }
  }

  @Test
  public void testWrongCommandsSet1() {
    Stream.of(
            "bye",
            "COUNT_LIST;x0:1:0.001:100;",
            "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)a",
            "MAX_LIST;x0:0:0,1:2;(x0+1)"
    ).forEach(input -> Assertions.assertThrows(MalformedCommandException.class, () -> {
      parser.parse(input);
    }));
  }

  @Test
  public void testCommand() {
    String input = "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));y1";

    ComputeCommand command = (ComputeCommand) parser.parse(input);
    assertEquals(CommandType.COMPUTE, command.getType());
    assertEquals(ComputationType.MIN, command.getComputationType());
    assertEquals(ValueType.GRID, command.getValueType());
    assertEquals("((x0+(2.0^x1))/(1-x0))", command.getExpressionsList().get(0));
    assertEquals("y1", command.getExpressionsList().get(1));
  }

}
