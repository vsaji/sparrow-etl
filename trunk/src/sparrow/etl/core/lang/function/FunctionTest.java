package sparrow.etl.core.lang.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Date;
import java.util.Iterator;

import sparrow.etl.core.dao.impl.QueryObject;
import sparrow.etl.core.dao.util.QueryExecutionStrategy;
import sparrow.etl.core.util.SparrowUtil;

public class FunctionTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    List aa = new ArrayList();
    aa.add("func_ternary((func_isnull(${s})),func_concatenate(\" and tnum > \"|${token1}),func_concatenate(\" and tnum > \"|${abc}|\"   \"|func_eval((2+3)+(3+2)*(3+2))))");
    aa.add("My Test Result is = func_substring(\"ESTAB,,LISHMENT\",(2+3),(func_length(${sabc})-3))");
    aa.add("'func_replace(func_lcase(func_trim(   @{token1}  )),tab,ggg)'   func_substring(func_lcase(ESTABLISHMENT),3,func_length(ESTABLISHMENT)) func_substring(func_lcase(func_trim( SAJI )),0,3) func_ucase(func_substring(func_trim( rules ),0,3)) we have one more func_ucase(saji) some more will come");
    aa.add("func_ternary((func_length(func_trim( SAJI ))==func_length(func_trim( SHIJI ))),\"SAJI\",\"SHIJI\")");
    aa.add("func_ternary((func_getdate()==func_getdate()),\"SAJI\",\"SHIJI\")");
    aa.add("func_nullval(func_lcase(func_trim(${token})),func_lcase(\"SAJI\"))");
    aa.add("func_rpad(\"SAJI\",\"10\",\"~\")");
    aa.add("func_enclose(func_rpad(\"SAJI\",\"10\",\" \"),#)");
    aa.add("func_enclose(func_lpad(\"SAJI\",\"10\",\" \"),#)");
    aa.add("func_eval((2+3)+(3+2)*(3+2))");
    aa.add("func_evaldouble((2.345+3.322)+(3.55+2.78)*(3.34+2.1223))");
    aa.add("func_evaldouble(func_evaldouble(3.5+1.2)*func_evaldouble(${abc}*1.2)*func_evaldouble(${abc}*1.2))");
    aa.add("func_formatdate(func_getdate(12-10-2009),\"EEE, MMM d, yy\")");
    aa.add("func_getdate(\"12-10-2009 09:30\",\"dd-MM-yyyy HH:mm\")");
    aa.add("func_adddate(func_getdate(\"12-10-2009 09:30\",\"dd-MM-yyyy HH:mm\"),minute,10)");
    aa.add("func_formatdate(func_adddate(func_getdate(\"12-10-2009 09:30\",\"dd-MM-yyyy HH:mm\"),minute,10),\"EEE, MMM d, yy HH:mm\")");
    aa.add("func_formatdecimal(func_evaldouble(85.545+78.25855),##.000)");
    aa.add("func_substring(func_evaldouble(85.545+78.25855),0,2)");
    aa.add("func_math(func_evaldouble(85.545+78.25855),R)");
    aa.add("func_evaldouble(func_convertdouble(\"78.25855\")+34)");
    aa.add("func_eval(func_convertnumber(\"78\")+34)");
    aa.add("func_indexof(\"SPARROW\",\"E\")");
    aa.add("func_math(func_evaldouble(85.545+func_convertdouble(${s})),A)");
    aa.add("func_concatenate(\"Today is | ${abc}\")");
    aa.add("func_isfileexist(\"c:/app/abc123.xml\")");    
    aa.add("func_isfileexist(${filename})");    

    // String g = "5-1";
    // int r = Integer.parseInt(g);
    SparrowUtil.loadImplConfig();

    for(Iterator it=aa.iterator();it.hasNext();){
      String a = it.next().toString();
      System.out.println("FUNC:"+a);
    QueryExecutionStrategy aa1 = QueryExecutionStrategy.getStrategy("test",
        a);
        QueryObject qo = new QueryObject();
        qo.getQueryParamAsMap().put("token1", "SAJI");
        qo.getQueryParamAsMap().put("abc", new Double(3455.22334));
        qo.getQueryParamAsMap().put("sabc", "ESTAB,,LISHMENT");
        qo.getQueryParamAsMap().put("date", new Date());
        qo.getQueryParamAsMap().put("s", "45.30");
        qo.getQueryParamAsMap().put("filename", "c:/app/abc.xml");
        aa1.implementStrategy(qo);
        System.out.println(qo.getTransformedSQL());
    }

    // List functions = new ArrayList();
    //
    // String as = FunctionUtil.resolveFunctions(a,functions);

    // int openBrackets = -1;
    //
    // int totalLen = a.length();
    // int functionStartPos=a.indexOf("func_");
    // int bracketStartPos=a.indexOf("(",functionStartPos);
    //
    //
    // for (int i = bracketStartPos; i < totalLen; i++) {
    // char c = a.charAt(i);
    //
    //
    // if(c=='('){
    // openBrackets=(openBrackets==-1) ? 0 : openBrackets;
    // openBrackets++;
    // }
    // if(c==')'){
    // openBrackets--;
    // }
    //
    // if(openBrackets==0){
    // functions.add(a.substring(functionStartPos,i+1));
    //
    // functionStartPos=a.indexOf("func_",i);
    // if(functionStartPos!=-1){
    // i=a.indexOf("(",functionStartPos)-1;
    // openBrackets =-1;
    // }
    // else{
    // break;
    // }
    // }
    // }
    System.exit(0);
    // Expression[] e = new Expression[functions.size()];
    // long start = System.currentTimeMillis();
    // for (int i=0;i< functions.size();i++) {
    // String element = (String) functions.get(i);
    // e[i] = ExpressionResolverFactory.resolveExpression(element);
    // }
    // System.out.println("Total Time:"+(System.currentTimeMillis()-start));
    //
    // HashMap h = new HashMap();
    // h.put("token1","ESTABLISHMENT");

    //
    // start = System.currentTimeMillis();
    // for (int i = 0; i < e.length; i++) {
    // System.out.println("Functions=["+(((Function)e[i]).getFunctionName())+"]="+e[i].getValue(h));
    // }
    // System.out.println("Total Time:"+(System.currentTimeMillis()-start));
    // System.out.println("Functions="+functions.toString());
  }

}
