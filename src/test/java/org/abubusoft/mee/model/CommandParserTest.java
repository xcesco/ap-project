package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.model.commands.ComputationType;
import org.abubusoft.mee.model.commands.ComputeCommand;
import org.abubusoft.mee.model.commands.ValuesType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class CommandParserTest {

  CommandParser parser = new CommandParserImpl();

  @Test
  public void testBye() throws MalformedCommandException {
    parser.parse("BYE");
  }

  @Test
  public void testStatMaxTime() throws MalformedCommandException {
    parser.parse("STAT_MAX_TIME");
  }

  @Test
  public void testMaxGrid() throws MalformedCommandException {
    parser.parse("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
  }

  @Test
  public void testCountList() throws MalformedCommandException {
    parser.parse("COUNT_LIST;x0:1:0.001:100;x1");
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
    Assertions.assertEquals(command.getType(), CommandType.COMPUTE);
    Assertions.assertEquals(command.getComputationType(), ComputationType.MIN);
    Assertions.assertEquals(command.getValuesType(), ValuesType.GRID);
    Assertions.assertEquals(command.getExpressionsList().get(0), "((x0+(2.0^x1))/(1-x0))");
    Assertions.assertEquals(command.getExpressionsList().get(1), "y1");
  }

}
