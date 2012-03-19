package sparrow.elt.core.lang.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import sparrow.elt.core.exception.ParserException;
import sparrow.elt.core.util.Constants;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class AbstractExpression
    implements Expression {

  protected static final int QUOTE = 12;

  protected static final String DOUBLE_QUOTE = "DOUBLE_QUOTE";
  
  protected static final String SINGLE_QUOTE = "SINGLE_QUOTE";
  
  protected static final int EOL = 13;

  protected static final int ORDINARY = 14;

  protected static final int WHITESPACE = 15;

  protected static final int OPENBRACKET = 16;

  protected static final int CLOSEBRACKET = 17;

  protected static final int ARGSEPARATOR = 18;

  protected static final int EXPRESSION_SEPARATOR = 19;

  /*******DEFAULT IMPLEMENTATION****************************/
  public String getValue(Map values) {
    return null;
  }

  public boolean getBooleanValue(Map values) {
    return false;
  }

  public int getIntValue(Map values) {
    return 0;
  }

  public long getLongValue(Map values) {
    return 0;
  }

  public double getDoubleValue(Map values) {
    return 0;
  }

  public Date getDateValue(Map values) {
    return null;
  }

  public void parse(String expression) throws ParserException {
  }

  /*******DEFAULT IMPLEMENTATION****************************/
  /**
   *
   * @param arg
   * @return
   */
  protected final boolean isFunction(String arg) {
    return (arg.trim().startsWith(Constants.FUNCTION_TOKEN));
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final boolean isVariable(String arg) {
    return (arg.indexOf(Constants.TOKEN_START) != -1 || arg
            .indexOf(Constants.REPLACE_TOKEN_START) != -1);
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final boolean isEval(String arg) {
    return (arg.trim().startsWith("("));
  }

  /**
   *
   */
  public boolean isFunction() {
    return false;
  }


  /**
   *
   */
  public boolean isBooleanExpression(){
    return false;
  }

  /**
   *
   */
  public boolean isStringExpression(){
    return true;
  }

  /**
   *
   * @param c
   * @return
   */
  protected int categorise(char c) {
    switch (c) {
      case ' ':
      case '\r':
      case 0xff:
        return WHITESPACE;
      case '\n':
        return EOL; /* artificially applied to end of line */
      case '(':
        return OPENBRACKET;
      case ')':
        return CLOSEBRACKET;
      case ',':
        return ARGSEPARATOR;
      case '\"':
      case '\'':
        return QUOTE;
      case '*':
      case '-':
      case '+':
      case '/':
      case '=':
      case '!':
      case '>':
      case '<':
      case '|':
      case '&':
        return EXPRESSION_SEPARATOR;
      default:
        if (0x00 <= c && c <= 0x20) {
          return WHITESPACE;
        }
        else if (Character.isWhitespace(c)) {
          return WHITESPACE;
        }
        else {
          return ORDINARY;
        }
    } // end of switch
  } // end of categorise

  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToStringExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new StringExpression(var, true);
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToDateExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new DateExpression(var);
  }


  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToIntExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new IntExpression(var);
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToLongExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new LongExpression(var);
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToDoubleExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new DoubleExpression(var);
  }

  /**
   *
   * @param arg
   * @return
   */
  protected final Expression tokenToBooleanExpression(String arg) {
    String temp = arg.trim();
    String var = temp.substring(2, temp.length() - 1);
    return new BooleanExpression(var);
  }

  /**
   *
   * @param s
   * @return
   */
  protected final boolean isOperator(String s) {
    char c = s.trim().charAt(0);
    return (c == '+' || c == '-' || c == '*' || c == '/' || c == '='
				|| c == '<' || c == '>' || c == '!' || c=='|' || c=='&');
  }

  /**
   *
   * @param arg
   * @return
   */
  protected Expression evaluateStringArgument(String arg) {
    Expression e = null;
    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory
          .getEvalExceptionHandlerInstance(arg);
    }
    else if (isVariable(arg)) {
      e = tokenToStringExpression(arg);
    }
    else {
      e = new StringExpression(arg, false);
    }
    return e;
  }

  /**
   *
   * @param arg
   * @return
   */
  protected Expression evaluateDateArgument(String arg,String format)  throws ParserException {
    Expression e = null;
    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory
          .getEvalExceptionHandlerInstance(arg);
    }
    else if (isVariable(arg)) {
      e = tokenToDateExpression(arg);
    }
    else {
      try {
        format = (format==null) ? "dd-MM-yyyy" : format;
        Date d = new SimpleDateFormat(format).parse(arg);
        e = new DateExpression(d);

//        else{
//          Calendar c1 = Calendar.getInstance();
//          c1.add(Calendar.DATE,Integer.parseInt(arg));
//          d = c1.getTime();
//        }
        //e = new DateExpression(d);
      }
      catch (ParseException ex) {
        throw new ParserException(ex);
      }
    }
    return e;
  }

  /**
   *
   * @param arg String
   * @throws ParserException
   * @return Expression
   */
  protected Expression evaluateDateArgument(String arg) throws ParserException {
    return evaluateDateArgument(arg,"dd-MM-yyyy");
  }

  /**
   *
   * @param arg
   * @return
   */
  protected Expression evaluateIntArgument(String arg) {
    Expression e = null;

    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg);
    }
    else if (isVariable(arg)) {
         e = tokenToIntExpression(arg);
       }
    else {
      e = new IntExpression(Integer.parseInt(arg));
    }
    return e;
  }

  /**
   *
   * @param arg String
   * @return Expression
   */
  protected Expression evaluateLongArgument(String arg) {
    Expression e = null;

    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg,
          RT_LONG);
    }
    else if (isVariable(arg)) {
      e = tokenToLongExpression(arg);
    }
    else {
      e = new LongExpression(Long.parseLong(arg));
    }
    return e;
  }

  /**
   *
   * @param arg String
   * @return Expression
   */
  protected Expression evaluateDoubleArgument(String arg) {
    Expression e = null;

    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg,
          RT_DOUBLE);
    }
    else if (isVariable(arg)) {
      e = tokenToDoubleExpression(arg);
    }
    else {
      e = new DoubleExpression(Double.parseDouble(arg));
    }
    return e;
  }

  /**
   *
   * @param arg String
   * @return Expression
   */
  protected Expression evaluateBooleanArgument(String arg) {
    Expression e = null;

    if (isFunction(arg)) {
      e = ExpressionResolverFactory.resolveExpression(arg);
    }
    else if (isEval(arg)) {
      e = ExpressionResolverFactory.getEvalExceptionHandlerInstance(arg,
          RT_BOOLEAN);
    }
    else if (isVariable(arg)) {
      e = tokenToBooleanExpression(arg);
    }
    else {
      e = new BooleanExpression(Boolean.valueOf(arg).booleanValue());
    }
    return e;
  }

}
