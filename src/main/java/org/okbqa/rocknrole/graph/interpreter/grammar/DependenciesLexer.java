// Generated from Dependencies.g4 by ANTLR 4.3
package org.okbqa.rocknrole.graph.interpreter.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DependenciesLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__4=1, T__3=2, T__2=3, T__1=4, T__0=5, STRING=6, NEWLINE=7, WHITESPACE=8;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'"
	};
	public static final String[] ruleNames = {
		"T__4", "T__3", "T__2", "T__1", "T__0", "STRING", "NEWLINE", "WHITESPACE"
	};


	public DependenciesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Dependencies.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\n.\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\7\6\7\37\n\7\r\7\16\7 \3\b\5\b$\n\b\3\b\3\b"+
		"\3\t\6\t)\n\t\r\t\16\t*\3\t\3\t\2\2\n\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\3\2\4\f\2##%),-\60\60\62<AAC\\`ac|\u0082\0\4\2\13\13\"\"\60\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\3\23\3\2\2\2\5\25\3\2\2\2\7\27\3\2\2\2\t\31\3\2"+
		"\2\2\13\33\3\2\2\2\r\36\3\2\2\2\17#\3\2\2\2\21(\3\2\2\2\23\24\7\61\2\2"+
		"\24\4\3\2\2\2\25\26\7*\2\2\26\6\3\2\2\2\27\30\7+\2\2\30\b\3\2\2\2\31\32"+
		"\7.\2\2\32\n\3\2\2\2\33\34\7/\2\2\34\f\3\2\2\2\35\37\t\2\2\2\36\35\3\2"+
		"\2\2\37 \3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\16\3\2\2\2\"$\7\17\2\2#\"\3\2"+
		"\2\2#$\3\2\2\2$%\3\2\2\2%&\7\f\2\2&\20\3\2\2\2\')\t\3\2\2(\'\3\2\2\2)"+
		"*\3\2\2\2*(\3\2\2\2*+\3\2\2\2+,\3\2\2\2,-\b\t\2\2-\22\3\2\2\2\6\2 #*\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}