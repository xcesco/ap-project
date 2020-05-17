package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MMERuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class CommandAnalyzerTest {

  CommandAnalyzer analyzer = new CommandAnalyzer();

  @Test
  public void testBye() {
    analyzer.execute("BYE");
  }

  @Test
  public void testStatMaxTime() {
    analyzer.execute("STAT_MAX_TIME");
  }

  @Test
  public void testMaxGrid() {
    analyzer.execute("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
  }

  @Test
  public void testCountList() {
    analyzer.execute("COUNT_LIST;x0:1:0.001:100;x1");
  }

  @Test
  public void testWrongCommands() {
    Stream.of("bye",
            "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)",
            "COUNT_LIST;x0:1:0.001:100;",
            "MAX_LIST;x0:0:0,1:2;(x0+1)"
    ).forEach(input -> Assertions.assertThrows(MMERuntimeException.class, () -> analyzer.execute(input)));
  }
}
