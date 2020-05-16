package org.abubusoft.mme.grammar;

import org.abubusoft.mee.exceptions.MMERuntimeException;
import org.abubusoft.mee.model.CommandAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestCommandAnalyzer {

  @Test
  public void testBye() {
    CommandAnalyzer.execute("BYE");
  }

  @Test
  public void testStatMaxTime() {
    CommandAnalyzer.execute("STAT_MAX_TIME");
  }

  @Test
  public void testMaxGrid() {
    CommandAnalyzer.execute("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)");
  }

  @Test
  public void testCountList() {
    CommandAnalyzer.execute("COUNT_LIST;x0:1:0.001:100;x1");
  }

  @Test
  public void testWrongCommands() {
    Arrays.asList("bye",
            "MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)",
            "COUNT_LIST;x0:1:0.001:100;",
            "MAX_LIST;x0:0:0,1:2;(x0+1)"
    ).stream().forEach(input -> {
      Assertions.assertThrows(MMERuntimeException.class, () -> {
        CommandAnalyzer.execute(input);
      });
    });


  }


}
