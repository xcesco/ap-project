package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AvgGridCommandTest extends BaseCommandTest {

  public AvgGridCommandTest() {
    super(ComputationType.AVG, ValuesType.GRID);
  }

  @Test
  public void tesValidCommand() {
    verify(" x0:1:1:10 ;  x0 ; x0+2", 5.5);
    verify(" x0:1:1:10 , y0:1:1:10 ;  x0 + y0 ", 11.0);
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> verify(" x0:0:0:10 ;  x0 ", 5.5));
    verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1", 11.0);
    verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1 - 1", 10.0);
    Assertions.assertThrows(UndefinedVariableException.class, () -> verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1 - 1 ; y1", 10.0));
  }

}
