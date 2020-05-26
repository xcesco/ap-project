package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.compute.VariableValues;

public interface ExpressionEvaluatorService {
  double evaluate(VariableValues variableValues, String input);

  void validate(VariableValues variableValues, String input);
}
