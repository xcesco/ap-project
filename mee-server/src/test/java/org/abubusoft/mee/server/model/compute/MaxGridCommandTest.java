package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaxGridCommandTest extends BaseCommandTest {
  public MaxGridCommandTest() {
    super(ComputationType.MAX, ValuesType.GRID);
  }


  @Test
  public void tesValidCommand() throws MalformedCommandException {
    verify("x0:1:1:10;x0", 10.0);
    verify("x0:1:1:10,y0:1:1:10;x0;y0+x0", 20.0);
    verify("x0:1:1:10,y0:1:1:10;y0+x0;x0", 20.0);
  }

  @Test
  public void testInvalidExpression() {
    Assertions.assertThrows(MalformedCommandException.class, () -> verify(" x0:1:1:10 , y0:1:1:10 ; x0 ; y0 + ", 1_000.0));
  }

}
