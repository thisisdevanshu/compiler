package cop5556sp18;
/* *
 * Initial code for Parser for the class project in COP5556 Programming Language Principles
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



import cop5556sp18.AST.*;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Scanner.Kind;

import java.util.ArrayList;
import java.util.List;

import static cop5556sp18.Scanner.Kind.*;


public class Parser {

	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}



	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public Program parse() throws SyntaxException {
		Program program = program();
		matchEOF();
		return  program;
	}

	/*
	 * Program ::= Identifier Block
	 */
	public Program program() throws SyntaxException {
		Token firstToken = t;
		match(IDENTIFIER);
		Block block = block();
		return new Program(firstToken, firstToken, block);
	}

	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */

	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = {KW_input, KW_write, KW_while, IDENTIFIER, KW_if, KW_show, KW_sleep, KW_red, KW_green, KW_blue,KW_alpha };
	Kind[] functionName = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r,
			KW_int, KW_float, KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha};
	Kind[] predefinedName = {KW_Z, KW_default_height, KW_default_width};

	public Block block() throws SyntaxException {
		Token firstToken = t;
		List<ASTNode>  decsOrStatements = new ArrayList<>();
		match(LBRACE);
		while (isKind(firstDec)|| isKind(firstStatement)) {
			if (isKind(firstDec)) {
				Declaration declaration = declaration();
				decsOrStatements.add(declaration);
			} else if (isKind(firstStatement)) {
				Statement statement = statement();
				decsOrStatements.add(statement);
			}
			match(SEMI);
		}
		match(RBRACE);
		return new Block(firstToken, decsOrStatements);
	}

	public Declaration declaration() throws SyntaxException{


		Token firstToken = t;
		Token type = t;
		Token name = null;
		Expression width = null;
		Expression height = null;
		if(isKind(KW_image)){
			match(KW_image);
			name = t;
			match(IDENTIFIER);
			if(isKind(LSQUARE)) {
				match(LSQUARE);
				width = expression();
				match(COMMA);
				height = expression();
				match(RSQUARE);
			}
		}else if( isKind(KW_int) || isKind(KW_float) || isKind(KW_boolean) || isKind(KW_filename)  ) {
			type();
			name = t;
			match(IDENTIFIER);
		}else{
			throw new SyntaxException(t,"Wrong way of declaring type");
		}
		return new Declaration(firstToken,type,name,width,height);
	}

	public Statement statement() throws SyntaxException{

		Statement statement = null;
		if(isKind(KW_input)){
			statement = statementInput();
		}else if(isKind(KW_write)){
			statement = statementWrite();
		}else if(isKind(IDENTIFIER) || isKind(KW_red) || isKind(KW_green) || isKind(KW_blue) || isKind(KW_alpha)){
			statement = statementAssignment();
		}else if(isKind(KW_while)) {
			statement = statementWhile();
		}else if(isKind(KW_if)){
			statement = statementIf();
		}else if(isKind(KW_show)){
			statement = statementShow();
		}else if(isKind(KW_sleep)){
			statement = statementSleep();
		}else{
			throw new SyntaxException(t,"Invalid statement");
		}
		return statement;
	}

	public Expression expression() throws SyntaxException{
		Token firstToken = t;
		Expression expression = orExpression();
		if(isKind(OP_QUESTION)){
			match(OP_QUESTION);
			Expression trueExpression = expression();
			match(OP_COLON);
			Expression falseExpression = expression();
			expression = new ExpressionConditional(firstToken,expression,trueExpression,falseExpression);
		}
		return expression;
	}

	public Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = andExpression();
		while(isKind(OP_OR)){
			Token op = t;
			match(OP_OR);
			Expression rightExpression = andExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression andExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression =  eqExpression();
		while(isKind(OP_AND)){
			Token op = t;
			match(OP_AND);
			Expression rightExpression =  eqExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression eqExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = relExpression();
		while(isKind(OP_EQ) || isKind(OP_NEQ)){
			Token op = t;
			if(isKind(OP_EQ)){
				match(OP_EQ);
			}else if(isKind(OP_NEQ)){
				match(OP_NEQ);
			}
			Expression rightExpression =  relExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression relExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = addExpression();
		while(isKind(OP_LT) || isKind(OP_LE) || isKind(OP_GT) || isKind(OP_GE)){
			Token op = t;
			if(isKind(OP_LT)){
				match(OP_LT);
			}else if(isKind(OP_LE)){
				match(OP_LE);
			}if(isKind(OP_GT)){
				match(OP_GT);
			}else if(isKind(OP_GE)){
				match(OP_GE);
			}
			Expression rightExpression =  addExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression addExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = multExpression();
		while(isKind(OP_PLUS) || isKind(OP_MINUS)){
			Token op = t;
			if(isKind(OP_PLUS)){
				match(OP_PLUS);
			}else{
				match(OP_MINUS);
			}
			Expression rightExpression =  multExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}
	public Expression multExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = powerExpression();
		while(isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD)){
			Token op = t;
			if(isKind(OP_TIMES)){
				match(OP_TIMES);
			}else if(isKind(OP_DIV)){
				match(OP_DIV);
			}else  if(isKind(OP_MOD)){
				match(OP_MOD);
			}
			Expression rightExpression =  powerExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression powerExpression() throws SyntaxException {
		Token firstToken = t;
		Expression leftExpression = unaryExpression();
		if(isKind(OP_POWER)){
			Token op = t;
			match(OP_POWER);
			Expression rightExpression =  powerExpression();
			leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
		}
		return leftExpression;
	}

	public Expression unaryExpression() throws SyntaxException {
		Token firstToken = t;
		Token op = t;
		Expression expression = null;
		if(isKind(OP_PLUS)){
			match(OP_PLUS);
			expression = unaryExpression();
			expression = new ExpressionUnary(firstToken,op,expression);
		}else if(isKind(OP_MINUS)){
			match(OP_MINUS);
			expression = unaryExpression();
			expression = new ExpressionUnary(firstToken,op,expression);
		}else{
			if(isKind(OP_EXCLAMATION)){
				match(OP_EXCLAMATION);
				expression = unaryExpression();
				expression = new ExpressionUnary(firstToken,op,expression);
			}else{
				expression = primary();
			}
		}
		return expression;
	}
	public Expression primary() throws SyntaxException {
		Token firstToken = t;
		Expression expression = null;
		if(isKind(INTEGER_LITERAL)){
			Token intLiteral = t;
			match(INTEGER_LITERAL);
			expression = new ExpressionIntegerLiteral(firstToken,intLiteral);
		}else if(isKind(BOOLEAN_LITERAL)){
			Token booleanLiteral = t;
			match(BOOLEAN_LITERAL);
			expression = new ExpressionBooleanLiteral(firstToken,booleanLiteral);
		}else if(isKind(FLOAT_LITERAL)){
			Token floatLiteral = t;
			match(FLOAT_LITERAL);
			expression = new ExpressionFloatLiteral(firstToken,floatLiteral);
		}else if(isKind(LPAREN)){
			match(LPAREN);
			expression = expression();
			match(RPAREN);
		}else if(isKind(functionName)){
			expression = functionApplication();
		}else if(isKind(IDENTIFIER)){
			Token name = t;
			match(IDENTIFIER);
			expression = new ExpressionIdent(firstToken,name);
			if(isKind(LSQUARE)){
				PixelSelector pixelSelector = pixelSelector();
				expression = new ExpressionPixel(firstToken,name,pixelSelector);
			}
		}else if(isKind(predefinedName)){
			Token name = t;
			if(isKind(KW_Z)){
				match(KW_Z);
			}else if(isKind(KW_default_height)){
				match(KW_default_height);
			}else if(isKind(KW_default_width)){
				match(KW_default_width);
			}
			expression = new ExpressionPredefinedName(firstToken,name);
		}else if(isKind(LPIXEL)){
			expression = pixelConstructor();
		}else{
			throw new SyntaxException(t,"Invalid primary "+t.kind+" "+t.posInLine());
		}
		return expression;
	}

	public Expression functionApplication() throws SyntaxException{
		Token firstToken = t;
		Token function = t;
		Expression expression = null;
		if(isKind(KW_sin) ){
			match(KW_sin);
		}else if(isKind(KW_cos)){
			match(KW_cos);
		}else if(isKind(KW_atan)){
			match(KW_atan);
		}else if(isKind(KW_abs)){
			match(KW_abs);
		}else if(isKind(KW_log)){
			match(KW_log);
		}else if(isKind(KW_cart_x)){
			match(KW_cart_x);
		}else if(isKind(KW_cart_y)){
			match(KW_cart_y);
		}else if(isKind(KW_polar_a)){
			match(KW_polar_a);
		}else if(isKind(KW_polar_r)){
			match(KW_polar_r);
		}else if(isKind(KW_int)){
			match(KW_int);
		}else if(isKind(KW_float)){
			match(KW_float);
		}else if(isKind(KW_width)){
			match(KW_width);
		}else if(isKind(KW_height)){
			match(KW_height);
		}else if(isKind(KW_red)){
			match(KW_red);
		}else if(isKind(KW_green)){
			match(KW_green);
		}else if(isKind(KW_blue)){
			match(KW_blue);
		}else if(isKind(KW_alpha)){
			match(KW_alpha);
		}

		if(isKind(LPAREN)){
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			expression = new ExpressionFunctionAppWithExpressionArg(firstToken,function,e);
		}else if(isKind(LSQUARE)){
			match(LSQUARE);
			Expression e0 = expression();
			match(COMMA);
			Expression e1 = expression();
			match(RSQUARE);
			expression = new ExpressionFunctionAppWithPixel(firstToken,function,e0,e1);
		}else{
			throw new SyntaxException(t,"Invalid function Application "+t.kind+" "+t.posInLine());
		}
		return expression;
	}

	public Expression pixelConstructor() throws SyntaxException{
		Token firstToken = t;
		match(LPIXEL);
		Expression alpha = expression();
		match(COMMA);
		Expression red = expression();
		match(COMMA);
		Expression green = expression();
		match(COMMA);
		Expression blue = expression();
		match(RPIXEL);
		return new ExpressionPixelConstructor(firstToken,alpha,red,green,blue);
	}

	public void type() throws SyntaxException{
		if(isKind(KW_int) ){
			match(KW_int);
		}else if(isKind(KW_float)){
			match(KW_float);
		}else if(isKind(KW_boolean)){
			match(KW_boolean);
		}else if(isKind(KW_filename)){
			match(KW_filename);
		}
	}

	public Statement statementInput() throws SyntaxException{
		Token firstToken = t;
		match(KW_input);
		Token destName = t;
		match(IDENTIFIER);
		match(KW_from);
		match(OP_AT);
		Expression expression = expression();
		return new StatementInput(firstToken, destName, expression);
	}

	public Statement statementWrite() throws SyntaxException{
		Token firstToken = t;
		match(KW_write);
		Token sourceName = t;
		match(IDENTIFIER);
		match(KW_to);
		Token destName = t;
		match(IDENTIFIER);
		return new StatementWrite(firstToken, sourceName, destName);
	}
	public Statement  statementAssignment() throws SyntaxException{
		Token firstToken = t;
		LHS lhs = LHS();
		match(OP_ASSIGN);
		Expression expression = expression();
		return new StatementAssign(firstToken,lhs,expression);
	}

	public Statement statementWhile() throws SyntaxException{
		Token firstToken = t;
		match(KW_while);
		match(LPAREN);
		Expression guard = expression();
		match(RPAREN);
		Block block = block();
		return new StatementWhile(firstToken,guard,block);
	}

	public Statement statementIf() throws SyntaxException{
		Token firstToken = t;
		match(KW_if);
		match(LPAREN);
		Expression guard = expression();
		match(RPAREN);
		Block block = block();
		return new StatementIf(firstToken, guard, block);
	}

	public Statement statementShow() throws SyntaxException{
		Token firstToken = t;
		match(KW_show);
		Expression expression = expression();
		return new StatementShow(firstToken, expression);
	}

	public Statement statementSleep() throws SyntaxException{
		Token firstToken = t;
		match(KW_sleep);
		Expression duration = expression();
		return new StatementSleep(firstToken, duration);
	}
	public LHS LHS() throws SyntaxException{
		LHS lhs = null;
		Token firstToken = t;
		if(isKind(IDENTIFIER)){
			Token name = t;
			match(IDENTIFIER);
			lhs = new LHSIdent(firstToken, name);
			if(isKind(LSQUARE)){
				PixelSelector pixelSelector = pixelSelector();
				lhs = new LHSPixel(firstToken,name,pixelSelector);
			}
		}else if(isKind(KW_red) || isKind(KW_green) || isKind(KW_blue) || isKind(KW_alpha) ){
			Token color = color();
			match(LPAREN);
			Token name = t;
			match(IDENTIFIER);
			PixelSelector pixelSelector = pixelSelector();
			match(RPAREN);
			lhs = new LHSSample(firstToken,name,pixelSelector,color);
		}else{
			throw new SyntaxException(t,"Invalid LHS");
		}
		return lhs;
	}

	public Token color() throws SyntaxException {
		Token color = t;
		if(isKind(KW_red)){
			match(KW_red);
		} else if(isKind(KW_green)){
			match(KW_green);
		} else if (isKind(KW_blue)){
			match(KW_blue);
		} else if (isKind(KW_alpha)){
			match(KW_alpha);
		}else {
			throw new SyntaxException(t, "Invalid color");
		}
		return color;
	}

	public PixelSelector pixelSelector() throws SyntaxException {
		Token firstToken = t;
		match(LSQUARE);
		Expression ex = expression();
		match(COMMA);
		Expression ey = expression();
		match(RSQUARE);
		return new PixelSelector(firstToken, ex, ey);
	}

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}


	/**
	 * Precondition: kind != EOF
	 *
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		throw new SyntaxException(t,"Syntax Error. Expected "+t.kind+" found "+kind+" at Line "+t.line()+" position "+ t.posInLine());
	}


	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind( EOF)) {
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
			//Note that EOF should be matched by the matchEOF method which is called only in parse().
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 *
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}


}

