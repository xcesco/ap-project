package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandsParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Pair;

public class ExpressionEvaluator implements Evaluator<ExpressionContext, Double> {
  public Double execute(ExpressionContext context, String input) throws MalformedCommandException {
    Pair<ParserRuleContext, CommonTokenStream> parser = Evaluator.buildParser(input, CommandsParser::expressions);
    ExpressionVisitor visitor = new ExpressionVisitor(context);
    return visitor.visit(parser.a);
  }
}
