package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.compute.ValuesTuple;

public interface ExpressionEvaluatorService {
  double evaluate(ValuesTuple valuesTuple, String input);

  void validate(ValuesTuple valuesTuple, String input);
}
