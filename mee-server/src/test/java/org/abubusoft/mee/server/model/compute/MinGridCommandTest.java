package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.junit.jupiter.api.Test;

public class MinGridCommandTest extends BaseCommandTest {
  public MinGridCommandTest() {
    super(ComputationType.MIN, ValueType.GRID);
  }

  @Test
  public void tesValidCommand() throws MalformedCommandException {
    verify("x0:1:1:10;(0-x0)", -10.0);
    verify("x0:1:1:10,y0:1:1:10;(0-x0);(0-(y0+x0))", -20.0);
    verify("x0:1:1:10,y0:1:1:10;(0-(y0+x0));(0-x0)", -20.0);
    verify("x0:.1:1:.10,y0:.0:1:1.0e2;(0-(y0+x0));(0-x0)", -100.1);
    verify("x0:.1:1:.10,y0:.0:1:1.0e2;x0", .1);
  }
}
