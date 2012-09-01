/**
 *
 */
package sparrow.etl.core.lang.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.exception.ParserException;
import sparrow.etl.core.util.SparrowUtil;


/**
 * @author Saji
 *
 */
public class EvalExpressionHandler
    extends AbstractExpression {

  protected static final int STATE_START_EXPRESSION = 0;

  protected static final int STATE_CONTINUE_EXPRESSION = 1;

  protected static final int STATE_NEW_EXPRESSION = 2;

  protected static final int STATE_END_EXPRESSION = 3;

  protected static final int OPERATOR_ADD = 22;

  protected static final int OPERATOR_SUB = 23;

  protected static final int OPERATOR_MUL = 24;

  protected static final int OPERATOR_DIV = 25;

  protected static final int OPERATOR_EQ = 26;

  protected static final int OPERATOR_NEQ = 27;

  protected static final int OPERATOR_GT = 28;

  protected static final int OPERATOR_LT = 29;

  protected static final int OPERATOR_GT_EQ = 30;

  protected static final int OPERATOR_LT_EQ = 31;

  protected static final int OPERATOR_AND = 32;

  protected static final int OPERATOR_OR = 33;

  Expression[] expressions = null;
  List operators = new ArrayList();
  int returnType = RT_INT;

  /**
   *
   */
  public EvalExpressionHandler() {
    super();
  }

  /**
   *
   * @param returnType int
   */
  public EvalExpressionHandler(int returnType) {
    this.returnType = returnType;
  }

  /**
   *
   * @param values Map
   * @return String
   */
  public String getValue(Map values) {
    return (returnType == RT_DOUBLE) ? String.valueOf(getDoubleValue(values)) :
        String.valueOf(getLongValue(values));
  }

  /**
   *
   * @param values Map
   * @return long
   */
  public long getLongValue(Map values) {

    boolean start = true;
    long result = -1;

    for (int i = 0, j = 0; i < expressions.length && j < operators.size(); ) {

      long lft;

      if (start) {
        Expression left = expressions[i++];
        lft = left.getLongValue(values);
        start = false;
      }
      else {
        lft = result;
      }

      Expression right = expressions[i++];
      int oprtor = ( (Integer) operators.get(j++)).intValue();

      long rgt = right.getLongValue(values);

      switch (oprtor) {
        case OPERATOR_ADD:
          result = (lft + rgt);
          break;
        case OPERATOR_SUB:
          result = (lft - rgt);
          break;
        case OPERATOR_MUL:
          result = (lft * rgt);
          break;
        case OPERATOR_DIV:
          result = (lft / rgt);
          break;
        case OPERATOR_EQ:
          result = (lft == rgt) ? 0 : 1;
          break;
        case OPERATOR_NEQ:
          result = (lft != rgt) ? 0 : 1;
          break;
        case OPERATOR_GT:
          result = (lft > rgt) ? 0 : 1;
          break;
        case OPERATOR_LT:
          result = (lft < rgt) ? 0 : 1;
          break;
        case OPERATOR_LT_EQ:
          result = (lft <= rgt) ? 0 : 1;
          break;
        case OPERATOR_GT_EQ:
          result = (lft >= rgt) ? 0 : 1;
          break;
      }
    }
    return result;
  }

  /**
   *
   * @param values Map
   * @return int
   */
  public int getIntValue(Map values) {
    boolean start = true;
    int result = -1;

    for (int i = 0, j = 0; i < expressions.length && j < operators.size(); ) {

      int lft;

      if (start) {
        Expression left = expressions[i++];
        lft = left.getIntValue(values);
        start = false;
      }
      else {
        lft = result;
      }

      Expression right = expressions[i++];
      int oprtor = ( (Integer) operators.get(j++)).intValue();

      int rgt = right.getIntValue(values);

      switch (oprtor) {
        case OPERATOR_ADD:
          result = (lft + rgt);
          break;
        case OPERATOR_SUB:
          result = (lft - rgt);
          break;
        case OPERATOR_MUL:
          result = (lft * rgt);
          break;
        case OPERATOR_DIV:
          result = (lft / rgt);
          break;
        case OPERATOR_EQ:
          result = (lft == rgt) ? 0 : 1;
          break;
        case OPERATOR_NEQ:
          result = (lft != rgt) ? 0 : 1;
          break;
        case OPERATOR_GT:
          result = (lft > rgt) ? 0 : 1;
          break;
        case OPERATOR_LT:
          result = (lft < rgt) ? 0 : 1;
          break;
        case OPERATOR_LT_EQ:
          result = (lft <= rgt) ? 0 : 1;
          break;
        case OPERATOR_GT_EQ:
          result = (lft >= rgt) ? 0 : 1;
          break;
      }
    }
    return result;
  }

  /**
   *
   * @param values Map
   * @return double
   */
  public double getDoubleValue(Map values) {
    boolean start = true;
    double result = -1;

    for (int i = 0, j = 0; i < expressions.length && j < operators.size(); ) {

      double lft;

      if (start) {
        Expression left = expressions[i++];
        lft = left.getDoubleValue(values);
        start = false;
      }
      else {
        lft = result;
      }

      Expression right = expressions[i++];
      int oprtor = ( (Integer) operators.get(j++)).intValue();

      double rgt = right.getDoubleValue(values);

      switch (oprtor) {
        case OPERATOR_ADD:
          result = (lft + rgt);
          break;
        case OPERATOR_SUB:
          result = (lft - rgt);
          break;
        case OPERATOR_MUL:
          result = (lft * rgt);
          break;
        case OPERATOR_DIV:
          result = (lft / rgt);
          break;
        case OPERATOR_EQ:
          result = (lft == rgt) ? 0 : 1;
          break;
        case OPERATOR_NEQ:
          result = (lft != rgt) ? 0 : 1;
          break;
        case OPERATOR_GT:
          result = (lft > rgt) ? 0 : 1;
          break;
        case OPERATOR_LT:
          result = (lft < rgt) ? 0 : 1;
          break;
        case OPERATOR_LT_EQ:
          result = (lft <= rgt) ? 0 : 1;
          break;
        case OPERATOR_GT_EQ:
          result = (lft >= rgt) ? 0 : 1;
          break;
      }
    }
    return result;
  }

  /**
   *
   */
  public boolean getBooleanValue(Map values) {
    return ( (getLongValue(values) == 0) ? true : false);
  }

  /**
   * @throws ParserException
   *
   */
  public void parse(String expression) throws ParserException {
    int openBrackets = 0;
    int exprStartPos = expression.indexOf("(");
    int exprEndPos = expression.lastIndexOf(")");
    int lcalState = STATE_START_EXPRESSION;

    StringBuffer sb = new StringBuffer();
    List args = new ArrayList();

    for (int i = exprStartPos + 1; i < exprEndPos; i++) {

      char c = expression.charAt(i);
      //System.out.println(i + "==" + c);
      int category = categorise(c);

      switch (lcalState) {
        /** **************************************************** */
        case STATE_START_EXPRESSION: {
          sb = new StringBuffer();
          switch (category) {
            case EOL:
              break;
            case WHITESPACE:
            case ARGSEPARATOR:
            case ORDINARY:
              sb.append(c);
              if ( (i + 1) == exprEndPos) {
                args.add(sb.toString());
              }
              lcalState = STATE_CONTINUE_EXPRESSION;
              break;
            case EXPRESSION_SEPARATOR:
              throw new ParserException(
                  "OPERATOR_NOT_EXPECTED_EVAL_ERROR",
                  "Operator is not allowed at the start-up of the expression");
            case OPENBRACKET:
              lcalState = STATE_NEW_EXPRESSION;
              openBrackets++;
              sb.append(c);
              break;
          }
          break;
        }
        /** **************************************************** */
        case STATE_CONTINUE_EXPRESSION: {
          switch (category) {
            case EOL:
              break;
            case WHITESPACE:
            case ARGSEPARATOR:
            case ORDINARY:
              sb.append(c);
              if ( (i + 1) == exprEndPos) {
                args.add(sb.toString());
              }
              break;
            case EXPRESSION_SEPARATOR:
              if (openBrackets == 0) {
                args.add(sb.toString());
                String a = String.valueOf(c); ;
                if (categorise(expression.charAt(i + 1)) ==
                    EXPRESSION_SEPARATOR) {
                  a = a + String.valueOf(expression.charAt(i + 1));
                  i++;
                }
                args.add(a);
                // System.out.println(sb.toString());
                lcalState = STATE_START_EXPRESSION;
              }
              else {
                sb.append(c);
              }
              break;
            case OPENBRACKET:
              openBrackets++;
              sb.append(c);
              break;
            case CLOSEBRACKET:
              openBrackets--;
              if (openBrackets == 0) {
                sb.append(c);
                args.add(sb.toString());
                lcalState = STATE_END_EXPRESSION;
              }
              else {
                sb.append(c);
              }
              break;
          }
          break;
        }
        /** **************************************************** */
        case STATE_NEW_EXPRESSION: {
          switch (category) {
            case EOL:
              break;
            case WHITESPACE:
            case ARGSEPARATOR:
            case ORDINARY:
            case EXPRESSION_SEPARATOR:
              sb.append(c);
              break;
            case OPENBRACKET:
              openBrackets++;
              sb.append(c);
              break;
            case CLOSEBRACKET:
              openBrackets--;
              if (openBrackets == 0) {
                sb.append(c);
                args.add(sb.toString());
                lcalState = STATE_END_EXPRESSION;
              }
              else {
                sb.append(c);
              }
              break;
          }
          break;
        }
        /** **************************************************** */
        case STATE_END_EXPRESSION: {
          switch (category) {
            case EOL:
              break;
            case WHITESPACE:
            case EXPRESSION_SEPARATOR:
              String a = String.valueOf(c); ;
              if (categorise(expression.charAt(i + 1)) == EXPRESSION_SEPARATOR) {
                a = a + String.valueOf(expression.charAt(i + 1));
                i++;
              }
              args.add(a);
              lcalState = STATE_START_EXPRESSION;
              break;
            default:
              throw new ParserException("OPERATOR_EXPECTED_EVAL_ERROR",
                                        "Operator [-,+,*,/] is expected");
          }
        }
      } // Main Switch
    } // For loop
    resolveArgumentType(args);
  } // Parse method

  /**
   *
   * @param args
   */
  void resolveArgumentType(List args) throws ParserException {
    List expr = new ArrayList();

    for (int i = 0; i < args.size(); i++) {
      String element = args.get(i).toString();

      if (isOperator(element)) {
        operators.add(new Integer(getOperator(element)));
        continue;
      }

      Expression e = getTypedArgument(element);
      expr.add(e);
    }
    expressions = (Expression[]) expr.toArray(new Expression[expr.size()]);
  }

  /**
   *
   * @param expr String
   * @return Expression
   */
  private Expression getTypedArgument(String expr) {
    switch (returnType) {
      case RT_DOUBLE:
        return evaluateDoubleArgument(expr);
      case RT_LONG:
        return evaluateLongArgument(expr);
      case RT_STRING:
        return evaluateStringArgument(expr);
      case RT_BOOLEAN:
        return evaluateBooleanArgument(expr);
      default:
        return evaluateIntArgument(expr);
    }
  }

  /**
   *
   * @param element
   * @return
   */
  protected int getOperator(String element) {

    String cs = element.trim();

    if (cs.equals("+")) {
      return OPERATOR_ADD;
    }
    else if (cs.equals("-")) {
      return OPERATOR_SUB;
    }
    else if (cs.equals("*")) {
      return OPERATOR_MUL;
    }
    else if (cs.equals("/")) {
      return OPERATOR_DIV;
    }
    else if (cs.equals("==")) {
      return OPERATOR_EQ;
    }
    else if (cs.equals("!=")) {
      return OPERATOR_NEQ;
    }
    else if (cs.equals(">")) {
      return OPERATOR_GT;
    }
    else if (cs.equals("<")) {
      return OPERATOR_LT;
    }
    else if (cs.equals("<=")) {
      return OPERATOR_LT_EQ;
    }
    else if (cs.equals(">=")) {
      return OPERATOR_GT_EQ;
    }
    else if (cs.equals("&&")) {
      return OPERATOR_AND;
    }
    else if (cs.equals("||")) {
      return OPERATOR_OR;
    }
    else {
      return 0;
    }
    // switch (c) {
    // case '+':
    // return OPERATOR_ADD;
    // case '-':
    // return OPERATOR_SUB;
    // case '*':
    // return OPERATOR_MUL;
    // case '/':
    // return OPERATOR_DIV;
    // default:
    // return 0;
    // }
  }

  /**
   *
   * @param args
   * @throws ParserException
   */
  public static void main(String[] args) throws ParserException {
    SparrowUtil.loadImplConfig();
    EvalExpressionHandler ee = new EvalExpressionHandler();
    // ee.parse("((func_len(SAJI)-1) * (func_len(SHIJI)-1))");
    // ee.parse("((5*2)*(func_len(SHIJI)-1)+(8-1))");
    ee.parse(
        "func_evaldouble(func_evaldouble(3.5+1.2)*func_evaldouble(3.5*1.2))");
    // + (3*func_length(SAJIUIY))+ func_length(func_lcase(SHREYA)))");
    //ee.parse("(5>=7)");
    System.out.println(ee.getLongValue(new HashMap()));
  }

}
