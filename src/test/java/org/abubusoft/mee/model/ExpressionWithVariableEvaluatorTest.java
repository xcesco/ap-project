package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MMERuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionWithVariableEvaluatorTest {
  ExpressionEvaluator evaluator = new ExpressionEvaluator();

  @Test
  public void testNumbersEvaluation() {
    Assertions.assertThrows(MMERuntimeException.class, () -> evaluateExpression("x5", 1.0, 0, 1.0));
    evaluateExpression("x", 1.0, 0, 1.0);
    evaluateExpression("-x", 2.0, 0, -2);
    evaluateExpression("1e2*y1", 0, 2, 200.0);
    evaluateExpression("x+y1", 1, 2, 3.0);
  }

  @Test
  public void testNumbersSumSubEvaluation() {
    evaluateExpression("x+1", 1, 0, 2.0);
    evaluateExpression("-1+y1", 0, 0, -1.0);
    evaluateExpression("-1+x+2+y1", 1, 2, 4.0);
  }

  @Test
  public void testNumbersMulDivEvaluation() {
    evaluateExpression("x*y1", 2, 4, 8.0);
    evaluateExpression("-x*2", 4, 0, -8.0);
    evaluateExpression("x/1", 2, 0, 2.0);
    evaluateExpression("8/x/2", 2, 0, 2.0);

    Assertions.assertThrows(MMERuntimeException.class, () -> evaluateExpression("2/x", 0, 0, 2.0));
  }

  @Test
  public void testNumbers4OpsEvaluation() {
    evaluateExpression("1+x*2", 2, 0, 5.0);
    evaluateExpression("(1+y1)*2", 0, 2, 6.0);
    evaluateExpression("-(x+y1)*2", 1, 2, -6.0);
    evaluateExpression("(-1+x)*y1", 2, 3, 3.0);
  }

  @Test
  public void testNumbersPowEvaluation() {
    evaluateExpression("x^y1", 2, 1, 2.0);
    evaluateExpression("y1^x^2", 2, 2, 16.0);
    evaluateExpression("2^y1*y1", 0, 2, 8.0);
  }

  @Test
  public void testMixEvaluation() {
    evaluateExpression("(1+x)^2", 1, 1, 4.0);
    evaluateExpression("(y1+1)^2-1", 1, 1, 3.0);
  }

  private void evaluateExpression(String input, double variableValueX, double variableValueY1, double aspectedValue) {
    double evaluationResult = evaluator.execute(ExpressionContext.Builder.create()
            .add("x", variableValueX)
            .add("y1", variableValueY1)
            .build(), input);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}