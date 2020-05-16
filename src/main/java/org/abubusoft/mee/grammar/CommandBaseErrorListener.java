package org.abubusoft.mee.grammar;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

public class CommandBaseErrorListener implements ANTLRErrorListener {

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.ANTLRErrorListener#syntaxError(org.antlr.v4.runtime.Recognizer, java.lang.Object, int, int, java.lang.String, org.antlr.v4.runtime.RecognitionException)
	 */
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
													String msg, RecognitionException e) {
		
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.ANTLRErrorListener#reportAmbiguity(org.antlr.v4.runtime.Parser, org.antlr.v4.runtime.dfa.DFA, int, int, boolean, java.util.BitSet, org.antlr.v4.runtime.atn.ATNConfigSet)
	 */
	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
															BitSet ambigAlts, ATNConfigSet configs) {
		
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.ANTLRErrorListener#reportAttemptingFullContext(org.antlr.v4.runtime.Parser, org.antlr.v4.runtime.dfa.DFA, int, int, java.util.BitSet, org.antlr.v4.runtime.atn.ATNConfigSet)
	 */
	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
			BitSet conflictingAlts, ATNConfigSet configs) {
		
	}

	/* (non-Javadoc)
	 * @see org.antlr.v4.runtime.ANTLRErrorListener#reportContextSensitivity(org.antlr.v4.runtime.Parser, org.antlr.v4.runtime.dfa.DFA, int, int, int, org.antlr.v4.runtime.atn.ATNConfigSet)
	 */
	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
			ATNConfigSet configs) {
		
	}

}