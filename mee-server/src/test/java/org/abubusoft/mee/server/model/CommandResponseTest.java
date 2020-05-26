package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.abubusoft.mee.server.services.impl.ExpressionEvaluatorServiceImpl;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandResponseTest {
  ClientRequestParser parser = new ClientRequestParserImpl(new ExpressionEvaluatorServiceImpl());

  @Test
  public void testOkResponse() {
    //checkCommand("MAX_GRID ; x0:-1:2:100000000000000000000000000000000000000000000000000 ;-1+2", "1.000000");
    checkCommand("MAX_GRID ; x0:-1:2:0 ;-1+2", "1.000000");
    checkCommand("MAX_GRID ; x0:-1:2:0 ;1+2*3", "7.000000");
    checkCommand("MAX_GRID ; x0:-1:2:0 ;x0^0.5", "NaN");
    checkCommand("MAX_GRID ; x0:-1:2:0 ;x0^0.5", "NaN");
    checkCommand("MAX_GRID ; x0:1:2:2 ;x0^(1)", "1.000000");
    checkCommand("MAX_GRID ; x0:0:2:1 ;x0", "0.000000");
    checkCommand("MAX_GRID ; x0:-1:0.1:1 ;x0", "1.000000");

    checkCommand("MAX_GRID ; x0:99999:1:100000 ; x0^x0", "Infinity");
    checkCommand("MIN_GRID ; x0:99999:1:100000 ; -x0^x0", "-Infinity");
  }

  @Test
  public void testDivisionBy0() {
    checkErrorCommand("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;x1;1/(x0+2.0^x1)", "(EvaluationExpressionException) Division by 0 in '1/(x0+2.0^x1)' with (x0=-1.000000, x1=0.000000)");
  }

  private void checkErrorCommand(String inputLine, String result) {
    ComputeCommand command = parser.parse(inputLine);
    CommandResponse response = null;
    try {
      command.execute();
    } catch (AppRuntimeException e) {
      response = CommandResponse.error(e);
    }
    Assertions.assertEquals("ERR;" + result, CommandResponseUtils.format(response));
  }

  private void checkCommand(String inputLine, String result) {
    ComputeCommand command = parser.parse(inputLine);
    CommandResponse response = command.execute();
    Assertions.assertEquals("OK;0.000;" + result, CommandResponseUtils.format(response));
    Assertions.assertEquals(ResponseType.OK, response.getResponseType());
    Assertions.assertEquals(0, response.getResponseTime());
    Assertions.assertEquals(null, response.getMessage());
  }

  @Test
  public void testErrorResponse() {
    CommandResponse response;
    try {
      Command command = parser.parse("MAX_GRID ; x0:-1:0.1:1 ;;x0a");
      response = command.execute();
    } catch (AppRuntimeException e) {
      response = CommandResponse.error(e);
    }
    Assertions.assertEquals("ERR;(MalformedCommandException) Unespected char at pos 24", CommandResponseUtils.format(response));
    Assertions.assertEquals(ResponseType.ERR, response.getResponseType());
    Assertions.assertEquals(0, response.getResponseTime());
    Assertions.assertEquals(0, response.getValue());
  }
}
