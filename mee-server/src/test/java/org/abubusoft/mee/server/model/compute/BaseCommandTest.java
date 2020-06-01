package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.ResponseType;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.junit.jupiter.api.Assertions;

public abstract class BaseCommandTest {
  public BaseCommandTest(ComputationType operation, ValueType valueType) {
    this.operation = operation;
    this.valueKind = valueType;
  }

  private final ClientRequestParser parser = new ClientRequestParserImpl();
  private final ComputationType operation;
  private final ValueType valueKind;

  public String prependType(String input) {
    return operation + "_" + valueKind + ";" + input;
  }

  protected void verify(String expression, double value) {
    ComputeCommand command = (ComputeCommand)parser.parse(prependType(expression));
    CommandResponse response = command.execute();

    Assertions.assertEquals(ResponseType.OK, response.getResponseType());
    Assertions.assertEquals(value, response.getValue());
  }
}
