package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.AppAssert;
import org.abubusoft.mee.exceptions.AppRuntimeException;
import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandBaseErrorListener;
import org.abubusoft.mee.grammar.CommandsBaseListener;
import org.abubusoft.mee.grammar.CommandsLexer;
import org.abubusoft.mee.grammar.CommandsParser;
import org.abubusoft.mee.model.commands.Command;
import org.abubusoft.mee.model.commands.CommandVisitor;
import org.abubusoft.mee.model.commands.ComputeCommand;
import org.abubusoft.mee.services.impl.LogExecutionTime;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;

@Component
public class CommandParserImpl implements CommandParser {
  interface RuleChooser {
    ParserRuleContext choose(CommandsParser parser) throws MalformedCommandException;
  }

  @LogExecutionTime
  public Command parse(String input) throws MalformedCommandException {
    ParserRuleContext parser = buildParser(input, CommandsParser::command);
    // check syntax errors
    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), buildParser(input, CommandsParser::parse));
    CommandVisitor visitor = new CommandVisitor();
    return visitor.visit(parser);
  }

  VariableDefinition parseVariableDefinition(String variableName, String input) throws MalformedCommandException {
    ParserRuleContext parser = buildParser(input, CommandsParser::variable_values);
    CommandVisitor visitor = new CommandVisitor();
    visitor.visit(parser);
    ComputeCommand command = visitor.getComputeBuilder().build();
    return command.getVariableDefinition(variableName);
  }

  double evaluateExpression(VariableValues variableValues, String input) throws MalformedCommandException {
    ParserRuleContext parser = buildParser(input, CommandsParser::expression);
    ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
    return visitor.visit(parser);
  }

  public static ParserRuleContext buildParser(final String input, final RuleChooser chooser) throws MalformedCommandException {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new CommandBaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
        AppAssert.fail("unespected char at pos %s in command '%s'", charPositionInLine, input);
      }
    });

    try {
      ParserRuleContext context = chooser.choose(parser);
      return context;
    } catch (AppRuntimeException e) {
      throw new MalformedCommandException(e.getMessage());
    }

  }

}
