package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountGridCommandTest extends BaseCommandTest {
  public CountGridCommandTest() {
    super(ComputationType.COUNT, ValueType.GRID);
  }

  @Test
  public void testVariableDefinedTwice() {
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> verify("x0:1:1:10,x0:1:1:10,y2:1:1:10;x0", 1_000.0));
  }

  @Test
  public void tesValidCommand() {
    verify("x0:0:.1:1;x0;y0", 11.0);
    verify("x0:0:.1:1;y0;x0", 11.0);
    verify("x0:1:1:10;x0", 10.0);
    verify("x0:0:.1:1;x0", 11.0);
    verify("x0:0:.1:1;x0;x0", 11.0);
    //Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> );
    verify("x0:1:1:10,y0:1:1:10;x0", 100.0);
    verify("x0:1:1:10,y0:1:1:10,y2:1:1:10;x0", 1_000.0);
  }
}
