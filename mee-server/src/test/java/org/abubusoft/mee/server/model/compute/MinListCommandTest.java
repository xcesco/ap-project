package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.services.CommandParser;
import org.abubusoft.mee.server.services.impl.CommandParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinListCommandTest {
  private CommandParser parser = new CommandParserImpl(new ExpressionEvaluatorImpl());

  @Test
  public void testListWithWrongVariableSize() {
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      ComputeCommand command = parser.parse("MIN_LIST ; x0:0:1:9 , x1:0:1:10 ;  x0+x1 ");
      command.execute();
    });

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      ComputeCommand command = parser.parse("MIN_LIST ; x0:0:1:10 , x1:0:1:9 ;  x0+x1 ");
      command.execute();
    });
  }

  @Test
  public void testUndefinedVariableException() {
    Assertions.assertThrows(UndefinedVariableException.class, () -> {
      ComputeCommand command = parser.parse("MIN_LIST ; x0:0:1:9 ;  x0+x1 ");
      command.execute();
    });
  }

  @Test
  public void testValidCommand() throws MalformedCommandException {
    ComputeCommand command = parser.parse("MIN_LIST ; x0:0:1:9 ;  x0 ");
    command.execute();
  }
}
