package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.junit.jupiter.api.Test;

public class MaxListCommandTest extends BaseCommandTest {
  public MaxListCommandTest() {
    super(ComputationType.MAX, ValuesType.LIST);
  }

  @Test
  public void tesValidCommand() throws MalformedCommandException {
    verify("x0:1:1:10;x0", 10.0);
    verify("x0:1:1:10,y0:1:1:10;x0;(y0+x0)", 20.0);
    verify("x0:1:1:10,y0:1:1:10;(y0+x0);x0", 20.0);
  }
}
