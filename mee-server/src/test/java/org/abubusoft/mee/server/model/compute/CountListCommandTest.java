package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountListCommandTest extends BaseCommandTest {
  public CountListCommandTest() {
    super(ComputationType.COUNT, ValuesType.LIST);
  }

  @Test
  public void testValidCommand() throws MalformedCommandException {
    Assertions.assertThrows(UndefinedVariableException.class, () -> verify(" x0:0:.1:1 ;  x0 ; y0 ", 5.5));

    verify(" x0:1:1:10 ;  x0 ", 10.0);
    verify(" x0:1:1:10 , y0:1:1:10 ;  x0 ", 10.0);
    verify(" x0:1:1:10 , y0:1:1:10 , y2:1:1:10 ;  x0 ", 10.0);
  }

  @Test
  public void testExample() throws MalformedCommandException {
    verify("x0:1:0.001:100;x0", 10.0);
  }


}
