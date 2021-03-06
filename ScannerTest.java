 /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}
	


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}




	@Test
	public void testParens() throws LexicalException {
		String input = "()";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, RPAREN, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testSquares() throws LexicalException {
		String input = "[]";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LSQUARE, 0, 1, 1, 1);
		checkNext(scanner, RSQUARE, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testBraces() throws LexicalException {
		String input = "{}";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LBRACE, 0, 1, 1, 1);
		checkNext(scanner, RBRACE, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testLess() throws LexicalException {
		String input = "<<<=+-";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPIXEL, 0, 2, 1, 1);
		checkNext(scanner, OP_LE, 2, 2, 1, 3);
		checkNext(scanner, OP_PLUS, 4, 1, 1, 5);
		checkNext(scanner, OP_MINUS, 5, 1, 1, 6);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testEqual() throws LexicalException {
		String input = "== != :=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_EQ, 0, 2, 1, 1);
		checkNext(scanner, OP_NEQ, 3, 2, 1, 4);
		checkNext(scanner, OP_ASSIGN, 6, 2, 1, 7);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testDoubleStar() throws LexicalException {
		String input = "***";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_POWER, 0, 2, 1, 1);
		checkNext(scanner, OP_TIMES, 2, 1, 1, 3);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIdentifier() throws LexicalException {
		String input = "devanshu";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 8, 1, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIdentifierContainigKW() throws LexicalException {
		String input = "integer";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 7, 1, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testKeyword() throws LexicalException {
		String input = "int";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_int, 0, 3, 1, 1);
		checkNextIsEOF(scanner);
	}


	@Test
	public void testAssign() throws LexicalException {
		String input = "int a_b := 34;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_int, 0, 3, 1, 1);
		checkNext(scanner, IDENTIFIER, 4, 3, 1, 5);
		checkNext(scanner, OP_ASSIGN, 8, 2, 1, 9);
		checkNext(scanner, INTEGER_LITERAL, 11, 2, 1, 12);
		checkNext(scanner, SEMI, 13, 1, 1, 14);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIf() throws LexicalException {
		String input = "if( ifa < Zb )";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_if, 0, 2, 1, 1);
		checkNext(scanner, LPAREN, 2, 1, 1, 3);
		checkNext(scanner, IDENTIFIER, 4, 3, 1, 5);
		checkNext(scanner, OP_LT, 8, 1, 1, 9);
		checkNext(scanner, IDENTIFIER, 10, 2, 1, 11);
		checkNext(scanner, RPAREN, 13, 1, 1, 14);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIntegerOverFlow() throws LexicalException {
		String input = "12345625727272727281";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(0,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

	@Test
	public void testInteger() throws LexicalException {
		String input = "0001234562";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, INTEGER_LITERAL, 1, 1, 1, 2);
		checkNext(scanner, INTEGER_LITERAL, 2, 1, 1, 3);
		checkNext(scanner, INTEGER_LITERAL, 3, 7, 1, 4);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testFloat() throws LexicalException {
		String input = ".1234562";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL, 0, 8, 1, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testComment() throws LexicalException {
		String input = "/* dude */ boolean b := true;";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, KW_boolean, 11, 7, 1, 12);
		checkNext(scanner, IDENTIFIER, 19, 1, 1, 20);
		checkNext(scanner, OP_ASSIGN, 21, 2, 1, 22);
		checkNext(scanner, BOOLEAN_LITERAL, 24, 4, 1, 25);
		checkNext(scanner, SEMI, 28, 1, 1, 29);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testMultilineComment() throws LexicalException {
		String input = "/* \r dude */ boolean \n  b := true;";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, KW_boolean, 13, 7, 2, 10);
		checkNext(scanner, IDENTIFIER, 24, 1, 3, 3);
		checkNext(scanner, OP_ASSIGN, 26, 2, 3, 5);
		checkNext(scanner, BOOLEAN_LITERAL, 29, 4, 3, 8);
		checkNext(scanner, SEMI, 33, 1, 3, 12);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testOpenComment() throws LexicalException {
		String input = "/* \n dude  boolean \n  b := true;";
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(32,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

	@Test
	public void testTwoDotsNoSpace() throws LexicalException {
		String input = "0.5..true";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL, 0, 3, 1, 1);
		checkNext(scanner, DOT, 3, 1, 1, 4);
		checkNext(scanner, DOT, 4, 1, 1, 5);
		checkNext(scanner, BOOLEAN_LITERAL, 5, 4, 1, 6);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testNoSpace() throws LexicalException {
		String input = "cosatanlogabs";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 13, 1, 1);
		checkNextIsEOF(scanner);
	}


	@Test
	public void testDoubleDot() throws LexicalException {
		String input = "0..1";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, FLOAT_LITERAL, 0, 2, 1, 1);
		checkNext(scanner, FLOAT_LITERAL, 2, 2, 1, 3);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIdentifierNumber() throws LexicalException {
		String input = "dev1_$";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 6, 1, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testIdentifierStart() throws LexicalException {
		String input = "3dev1_$";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, IDENTIFIER, 1, 6, 1, 2);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testSingleChar() throws LexicalException {
		String input = "({[)}]+-<<>><=>=@!,.?|%*/&";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, LBRACE, 1, 1, 1, 2);
		checkNext(scanner, LSQUARE, 2, 1, 1, 3);
		checkNext(scanner, RPAREN, 3, 1, 1, 4);
		checkNext(scanner, RBRACE, 4, 1, 1, 5);
		checkNext(scanner, RSQUARE, 5, 1, 1, 6);
		checkNext(scanner, OP_PLUS, 6, 1, 1, 7);
		checkNext(scanner, OP_MINUS, 7, 1, 1, 8);
		checkNext(scanner, LPIXEL, 8, 2, 1, 9);
		checkNext(scanner, RPIXEL, 10, 2, 1, 11);
		checkNext(scanner, OP_LE, 12, 2, 1, 13);
		checkNext(scanner, OP_GE, 14, 2, 1, 15);
		checkNext(scanner, OP_AT, 16, 1, 1, 17);
		checkNext(scanner, OP_EXCLAMATION, 17, 1, 1, 18);
		checkNext(scanner, COMMA, 18, 1, 1, 19);
		checkNext(scanner, DOT, 19, 1, 1, 20);
		checkNext(scanner, OP_QUESTION, 20, 1, 1, 21);
		checkNext(scanner, OP_OR, 21, 1, 1, 22);
		checkNext(scanner, OP_MOD, 22, 1, 1, 23);
		checkNext(scanner, OP_TIMES, 23, 1, 1, 24);
		checkNext(scanner, OP_DIV, 24, 1, 1, 25);
		checkNext(scanner, OP_AND, 25, 1, 1, 26);
		checkNextIsEOF(scanner);
	}


}
	

