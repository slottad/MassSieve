// $ANTLR 2.7.7 (20070412): "SetParser.g" -> "SetParser.java"$

package gov.nih.nimh.mass_sieve.io;
import gov.nih.nimh.mass_sieve.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class SetParser extends antlr.LLkParser       implements SetParserTokenTypes
 {

    private PeptideCollection pepCollect;

    public void setPeptideCollection(PeptideCollection pc) {
        pepCollect = pc;
    }

    


protected SetParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public SetParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected SetParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public SetParser(TokenStream lexer) {
  this(lexer,1);
}

public SetParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final PeptideCollection  expr() throws RecognitionException, TokenStreamException {
		PeptideCollection result = new PeptideCollection();
		
		PeptideCollection x;
		
		try {      // for error handling
			result=cexpr();
			{
			_loop3:
			do {
				switch ( LA(1)) {
				case UNION:
				{
					match(UNION);
					x=cexpr();
					result = result.union(x);
					break;
				}
				case DIFF:
				{
					match(DIFF);
					x=cexpr();
					result = result.difference(x);
					break;
				}
				case INTERSECT:
				{
					match(INTERSECT);
					x=cexpr();
					result = result.intersection(x);
					break;
				}
				default:
				{
					break _loop3;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return result;
	}
	
	public final PeptideCollection  cexpr() throws RecognitionException, TokenStreamException {
		PeptideCollection result = new PeptideCollection();
		
		PeptideCollection x;
		
		try {      // for error handling
			{
			_loop6:
			do {
				switch ( LA(1)) {
				case SET:
				case LPAREN:
				{
					x=atom();
					result = x;
					break;
				}
				case COMPLEMENT:
				{
					match(COMPLEMENT);
					x=atom();
					result = pepCollect.difference(x);
					break;
				}
				default:
				{
					break _loop6;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return result;
	}
	
	public final PeptideCollection  atom() throws RecognitionException, TokenStreamException {
		PeptideCollection result = new PeptideCollection();
		
		Token  sv = null;
		PeptideCollection x;
		char subset;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SET:
			{
				sv = LT(1);
				match(SET);
				
				subset=sv.getText().charAt(0);
				switch (subset) {
				case 'm':
				case 'M': { 
				result = pepCollect.getMascot();
				break;
				}
				case 'o':
				case 'O': {
				result = pepCollect.getOmssa();
				break;
				}
				case 'p':
				case 'P': {
				result = pepCollect.getPepXML();
				break;
				}
				case 'x':
				case 'X': {
				result = pepCollect.getXTandem();
				break;
				}
				case 's':
				case 'S': {
				result = pepCollect.getSequest();
				break;
				}
				}
				
				break;
			}
			case LPAREN:
			{
				match(LPAREN);
				x=expr();
				match(RPAREN);
				result=x;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return result;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"UNION",
		"DIFF",
		"INTERSECT",
		"COMPLEMENT",
		"SET",
		"LPAREN",
		"RPAREN",
		"WS"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 1024L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 1136L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2032L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
