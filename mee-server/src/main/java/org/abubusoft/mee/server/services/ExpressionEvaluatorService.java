package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.compute.VariablesValue;

public interface ExpressionEvaluatorService {
  double evaluate(VariablesValue variablesValue, String input);

  void checkVariablesInExpression(VariablesValue variablesValue, String input);
}
