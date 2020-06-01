package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.compute.Expression;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionWithVariablesTest {
  @Test
  public void testNumbersEvaluation() throws MalformedCommandException {
    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("x5", 1.0, 0, 1.0));
    evaluateExpression("x", 1.0, 0, 1.0);
    evaluateExpression("(0-x)", 2.0, 0, -2);
    evaluateExpression("(100*y1)", 0, 2, 200.0);
    evaluateExpression("(x+y1)", 1, 2, 3.0);
  }

  @Test
  public void testNumbersSumSubEvaluation() throws MalformedCommandException {
    evaluateExpression("(x+1)", 1, 0, 2.0);
    evaluateExpression("(y1-1)", 0, 0, -1.0);
    evaluateExpression("(((x-1)+2)+y1)", 1, 2, 4.0);
  }

  @Test
  public void testNumbersMulDivEvaluation() throws MalformedCommandException {
    evaluateExpression("(x*y1)", 2, 4, 8.0);
    evaluateExpression("(0-(x*2))", 4, 0, -8.0);
    evaluateExpression("(x/1)", 2, 0, 2.0);
    evaluateExpression("((8/x)/2)", 2, 0, 2.0);

    evaluateExpression("(2/x)", 0, 0, Double.POSITIVE_INFINITY);
  }

  @Test
  public void testNumbers4OpsEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+(x*2))", 2, 0, 5.0);
    evaluateExpression("((1+y1)*2)", 0, 2, 6.0);
    evaluateExpression("(0-((x+y1)*2))", 1, 2, -6.0);
    evaluateExpression("((x-1)*y1)", 2, 3, 3.0);
  }

  @Test
  public void testNumbersPowEvaluation() throws MalformedCommandException {
    evaluateExpression("(x^y1)", 2, 1, 2.0);
    evaluateExpression("((y1^x)^2)", 2, 2, 16.0);
    evaluateExpression("((2^y1)*y1)", 0, 2, 8.0);
  }

  @Test
  public void testVariableUndefined() throws MalformedCommandException {
    Assertions.assertThrows(UndefinedVariableException.class, () -> evaluateExpression("(x^y2)", 2, 1, 2.0));
  }

  @Test
  public void testMixEvaluation() throws MalformedCommandException {
    evaluateExpression("((1+x)^2)", 1, 1, 4.0);
    evaluateExpression("(((y1+1)^2)-1)", 1, 1, 3.0);
  }

  private void evaluateExpression(String input, double variableValueX, double variableValueY1, double aspectedValue) throws MalformedCommandException {
    MultiVariableValue values = MultiVariableValue.Builder.create()
            .add("x", variableValueX)
            .add("y1", variableValueY1)
            .build();
    Expression expression = ComputeCommand.buildExpression(values, input);
    double evaluationResult = expression.evaluate(values);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}