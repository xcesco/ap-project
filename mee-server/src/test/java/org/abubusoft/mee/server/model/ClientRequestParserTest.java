package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.ComputationType;
import org.abubusoft.mee.server.model.compute.ValueType;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorImpl;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientRequestParserTest {

  ClientRequestParser parser = new ClientRequestParserImpl(new ExpressionEvaluatorImpl());

  @Test
  public void testBye() {
    Command command = parser.parse("BYE");
    assertEquals(command.getType(), CommandType.BYE);
  }

  @Test
  public void testStatMaxTime() {
    Command command = parser.parse("STAT_MAX_TIME");
    assertEquals(command.getType(), CommandType.STAT);
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
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testAvgGrid() {
    Command command = parser.parse("AVG_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testMinGrid() {
    Command command = parser.parse("MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testCountList() {
    {
      Command command = parser.parse("COUNT_LIST;x0:.0:0.001:100;x1");
      assertEquals(command.getType(), CommandType.COMPUTE);
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

    ComputeCommand command = parser.parse(input);
    assertEquals(command.getType(), CommandType.COMPUTE);
    assertEquals(command.getComputationType(), ComputationType.MIN);
    assertEquals(command.getValueType(), ValueType.GRID);
    assertEquals(command.getExpressionsList().get(0), "((x0+(2.0^x1))/(1-x0))");
    assertEquals(command.getExpressionsList().get(1), "y1");
  }

}
