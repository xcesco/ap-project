package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.ComputationType;
import org.abubusoft.mee.server.model.compute.ValuesType;
import org.abubusoft.mee.server.services.CommandParser;
import org.abubusoft.mee.server.services.impl.CommandParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandParserTest {

  CommandParser parser = new CommandParserImpl(new ExpressionEvaluatorImpl());

  @Test
  public void testBye() throws MalformedCommandException {
    Command command = parser.parse("BYE");
    assertEquals(command.getType(), CommandType.BYE);
  }

  @Test
  public void testStatMaxTime() throws MalformedCommandException {
    Command command = parser.parse("STAT_MAX_TIME");
    assertEquals(command.getType(), CommandType.STAT);
  }

  @Test
  public void testMaxGrid() throws MalformedCommandException {
    Command command = parser.parse("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testAvgGrid() throws MalformedCommandException {
    Command command = parser.parse("AVG_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testMinGrid() throws MalformedCommandException {
    Command command = parser.parse("MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }


  @Test
  public void testCountList() throws MalformedCommandException {
    Command command = parser.parse("COUNT_LIST;x0:1:0.001:100;x1");
    assertEquals(command.getType(), CommandType.COMPUTE);
  }

  @Test
  public void testWrongCommandsSet1() {
    Stream.of("bye",
            "COUNT_LIST;x0:1:0.001:100;",
            "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)a",
            "MAX_LIST;x0:0:0,1:2;(x0+1)"
    ).forEach(input -> Assertions.assertThrows(MalformedCommandException.class, () -> parser.parse(input)));
  }

  @Test
  public void testCommand() throws MalformedCommandException {
    String input = "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));y1";

    ComputeCommand command = parser.parse(input);
    assertEquals(command.getType(), CommandType.COMPUTE);
    assertEquals(command.getComputationType(), ComputationType.MIN);
    assertEquals(command.getValuesType(), ValuesType.GRID);
    assertEquals(command.getExpressionsList().get(0), "((x0+(2.0^x1))/(1-x0))");
    assertEquals(command.getExpressionsList().get(1), "y1");
  }

}
