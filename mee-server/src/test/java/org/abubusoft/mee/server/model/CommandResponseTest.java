package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.services.CommandParser;
import org.abubusoft.mee.server.services.impl.CommandParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorImpl;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandResponseTest {
  CommandParser parser = new CommandParserImpl(new ExpressionEvaluatorImpl());

  @Test
  public void testOKResponse() throws MalformedCommandException {
    Command command = parser.parse("MAX_GRID ; x0:-1:0.1:1 ;x0");
    CommandResponse response = command.execute();
    Assertions.assertEquals("OK;0.000;1.000000", CommandResponseUtils.format(response));
    Assertions.assertEquals(ResponseType.OK, response.getResponseType());
    Assertions.assertEquals(0, response.getResponseTime());
    Assertions.assertEquals(1.0, response.getValue());
    Assertions.assertEquals(null, response.getMessage());
  }

  @Test
  public void testERROResponse() throws MalformedCommandException {
    CommandResponse response;
    try {
      Command command = parser.parse("MAX_GRID ; x0:-1:0.1:1 ;;x0a");
      response = command.execute();
    } catch (MalformedCommandException | AppRuntimeException e) {
      response = CommandResponse.error(e);
    }
    Assertions.assertEquals("ERR;(MalformedCommandException) Unespected char at pos 24", CommandResponseUtils.format(response));
    Assertions.assertEquals(ResponseType.ERR, response.getResponseType());
    Assertions.assertEquals(0, response.getResponseTime());
    Assertions.assertEquals(0, response.getValue());
  }
}
