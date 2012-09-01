package sparrow.etl.core.lang.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sparrow.etl.core.util.Constants;
import sparrow.etl.core.util.SparrowUtil;


/**
 * @author Saji Venugopalan
 *
 */
public class FunctionUtil {


  /**
   *
   * @param text String
   * @param functions List
   * @return String
   */
  public static final String resolveFunctions(String text, List functions) {

		int openBrackets = -1;
		int totalLen = text.length();

		int functionStartPos = text.indexOf(Constants.FUNCTION_TOKEN);
		int bracketStartPos = text.indexOf("(", functionStartPos);

		List pos = new ArrayList();
		StringBuffer returnValue = new StringBuffer();

		for (int i = bracketStartPos; i < totalLen; i++) {
			char c = text.charAt(i);

			if (c == '(') {
				openBrackets = (openBrackets == -1) ? 0 : openBrackets;
				openBrackets++;
			}
			if (c == ')') {
				openBrackets--;
			}

			if (openBrackets == 0) {
				functions.add(text.substring(functionStartPos, i + 1));
				pos.add(functionStartPos+Constants.FUNCTION_REPLACE_TOKEN+i);
				functionStartPos = text.indexOf(Constants.FUNCTION_TOKEN, i);
				if (functionStartPos != -1) {
					i = text.indexOf("(", functionStartPos) - 1;
					openBrackets = -1;
				} else {
					break;
				}
			}
		}

		int strtPos = 0;
		for (int i=0;i< pos.size(); i++) {
			String element = (String) pos.get(i);
			String[] splt = element.split(Constants.FUNCTION_REPLACE_TOKEN);
			returnValue.append(text.substring(strtPos,Integer.parseInt(splt[0])));
			returnValue.append(Constants.FUNCTION_REPLACE_TOKEN);
			strtPos = Integer.parseInt(splt[1])+1;
		}
		returnValue.append(text.substring(strtPos,text.length()));
		return returnValue.toString();
	}

  /**
   *
   * @param functions List
   * @return Expression[]
   */
  public static final Expression[] getFunctions(List functions) {
    Expression[] e = new Expression[functions.size()];
    for (int i = 0; i < functions.size(); i++) {
      String element = (String) functions.get(i);
      e[i] = ExpressionResolverFactory.resolveExpression(element);
    }
    return e;
  }


  /**
   *
   * @param string String
   * @param values Map
   * @param functions Expression[]
   * @return String
   */
  public static String executeFunction(String string, Map values,Expression[] functions) {
      for (int k = 0; (string.indexOf(Constants.FUNCTION_REPLACE_TOKEN)!=-1); k++) {
        String result = functions[k].getValue(values);
        if (result == null) {
          result = "null";
        }
        string = SparrowUtil.replaceFirst(string,Constants.FUNCTION_REPLACE_TOKEN, result);
      }
      return string;
    }
}
