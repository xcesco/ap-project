package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AvgListCommandTest extends BaseCommandTest {

  public AvgListCommandTest() {
    super(ComputationType.AVG, ValuesType.LIST);
  }

  @Test
  public void testCommand() throws MalformedCommandException {
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> verify(" x0:1:1:10 , x1:1:1:11  ;  x0 + x1 - 1 ; x0^2", 10.0));
    verify(" x0:1:1:10 ;  x0 ", 5.5);
    verify(" x0:1:1:10 , a1:1:1:10  ;  x0 + a1", 11.0);
    verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1 - 1", 10.0);
    verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1 - 1 ; x0^2", 10.0);
    Assertions.assertThrows(UndefinedVariableException.class, () -> verify(" x0:1:1:10 , x1:1:1:10  ;  x0 + x1 - 1 ; y0^2", 10.0));
  }

}
