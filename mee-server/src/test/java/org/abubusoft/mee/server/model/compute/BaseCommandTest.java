package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.ResponseType;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorServiceImpl;
import org.junit.jupiter.api.Assertions;

public abstract class BaseCommandTest {
  public BaseCommandTest(ComputationType operation, ValuesType valuesType) {
    this.operation = operation;
    this.valueKind = valuesType;
  }

  private final ClientRequestParser parser = new ClientRequestParserImpl(new ExpressionEvaluatorServiceImpl());
  private ComputationType operation;
  private ValuesType valueKind;

  public String prependType(String input) {
    return operation + "_" + valueKind + ";" + input;
  }

  protected void verify(String expression, double value) {
    ComputeCommand command = parser.parse(prependType(expression));
    CommandResponse response = command.execute();

    Assertions.assertEquals(ResponseType.OK, response.getResponseType());
    Assertions.assertEquals(value, response.getValue());
  }
}