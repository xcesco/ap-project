package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.impl.ClientRequestParserImpl;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandResponseTest {
  ClientRequestParser parser = new ClientRequestParserImpl();

  @Test
  public void testMalformedRequest() {
    checkErrorCommand("ciao", "(MalformedCommandException) Unespected char at pos 0");
    checkErrorCommand("MAX_GRID;x0:0:1:0;x1", "(UndefinedVariableException) Undefined variable 'x1' is used in expression 'x1'");
    checkErrorCommand("", "(MalformedCommandException) No command specified");
    checkErrorCommand("   ", "(MalformedCommandException) No command specified");
    checkErrorCommand("b\t^H", "(MalformedCommandException) Unespected char at pos 1");
    checkErrorCommand("MAX_GRID ; x0:-1:2:0 ; (2-1)", "(MalformedCommandException) Unespected char at pos 8");
  }

  @Test
  public void testInvalidIntervalDefinition() {
    checkErrorCommand("COUNT_GRID;x0:0:1:-1;a1", "(InvalidVariableDefinitionException) Inconsistent variable definition 'x0'");
    checkErrorCommand("COUNT_GRID;x0:NaN:1:-1;a1", "(MalformedCommandException) Unespected char at pos 14");
    checkErrorCommand("COUNT_GRID;x0:Infinite:1:-1;a1", "(MalformedCommandException) Unespected char at pos 14");
    checkErrorCommand("COUNT_LIST;x0:0:1:10,x1:0:1:10,x2:0:1:11;(x0*2);(x0+2)", "(InvalidVariableDefinitionException) Variables 'x0' and 'x2' have different values range size (11, 12)");
  }

  @Test
  public void testValidIntervalDefinition() {
    checkCommand("MAX_GRID;x0:0:1:1,y0:1:.1:2;x0;y0", "2.000000");
    checkCommand("MAX_GRID;x0:0:1:1,y0:1:.1:2,z0:2:.1:3;x0;y0;z0", "3.000000");
    checkCommand("MAX_GRID;x0:0:1:1,y0:1:.1:2,z0:2:.1:3;x0;y0;(z0+x0)", "4.000000");
    checkCommand("COUNT_GRID;x0:0:1:1;a1", "2.000000");
    checkCommand("COUNT_GRID;x0:0.0:1:1.;a1", "2.000000");
    checkCommand("COUNT_GRID;x0:0e-1:1:1.E-1;a1", "1.000000");
    checkCommand("COUNT_GRID;x0:0e+1:.1:1.E1;a1", "101.000000");
    checkCommand("COUNT_GRID;x0:0.:.1:.0E1;a1", "1.000000");
    checkCommand("COUNT_GRID;x0:100:10:200;a1", "11.000000");
    checkCommand("COUNT_GRID;x0:110:10:200;a1", "10.000000");
  }


  @Test
  public void testValidMultipleIntervalDefinition() {
    checkErrorCommand("COUNT_GRID;x0:110:10:200,x0:110:10:200;a1", "(InvalidVariableDefinitionException) Variable 'x0' is defined twice");
    checkCommand("COUNT_GRID;x0:110:10:200,y0:110:10:200;a1", "100.000000");
  }

  @Test
  public void testOkResponse() {
    checkCommand("MAX_GRID;x0:-1:2:0;(2000-1990.0)", "10.000000");
    checkCommand("MIN_GRID;x0:-1:2:0;(2000-1990.0)", "10.000000");
    checkCommand("MAX_GRID;x0:-1:2:0;(2-1)", "1.000000");
    checkCommand("MAX_GRID;x0:-1:2:0;(2-1)", "1.000000");
    checkCommand("MAX_GRID;x0:-1:2:0;(1+(2*3))", "7.000000");
    checkCommand("MAX_GRID;x0:-1:2:0;(x0^0.5)", "NaN");
    checkCommand("MAX_GRID;x0:-1:2:0;(x0^0.5)", "NaN");
    checkCommand("MAX_GRID;x0:1:2:2;(x0^1)", "1.000000");
    checkCommand("MAX_GRID;x0:0:2:1;x0", "0.000000");
    checkCommand("MAX_GRID;x0:-1:0.1:1;x0", "1.000000");

    checkCommand("MAX_GRID;x0:99999:1:100000;(x0^x0)", "Infinity");
    checkCommand("MIN_GRID;x0:99999:1:100000;((0-1)*(x0^x0))", "-Infinity");
  }

  @Test
  public void testWrongExpressions() {
    checkErrorCommand("MAX_GRID;x0:-1:2:0;(200.0001-1.0E)", "(MalformedCommandException) Unespected char at pos 32");
  }

  @Test
  public void testBlank() {
    checkErrorCommand("MAX_GRID ; x0:-1:2:0 ;-1+2", "(MalformedCommandException) Unespected char at pos 8");
  }

  @Test
  public void testDivisionBy0() {
    checkCommand("MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;x1;(1/(x0+(2.0^x1)))", "Infinity");
  }

  private void checkErrorCommand(String inputLine, String result) {
    CommandResponse response;
    try {
      ComputeCommand command = (ComputeCommand) parser.parse(inputLine);
      response = command.execute();
    } catch (AppRuntimeException e) {
      response = CommandResponse.error(e);
    }
    Assertions.assertEquals("ERR;" + result, CommandResponseUtils.format(response));
  }

  private void checkCommand(String inputLine, String result) {
    ComputeCommand command = (ComputeCommand) parser.parse(inputLine);
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
      Command command = parser.parse("MAX_GRID;x0:-1:0.1:1;;x0a");
      response = command.execute();
    } catch (AppRuntimeException e) {
      response = CommandResponse.error(e);
    }
    Assertions.assertEquals("ERR;(MalformedCommandException) Unespected char at pos 21", CommandResponseUtils.format(response));
    Assertions.assertEquals(ResponseType.ERR, response.getResponseType());
    Assertions.assertEquals(0, response.getResponseTime());
    Assertions.assertEquals(0, response.getValue());
  }
}
