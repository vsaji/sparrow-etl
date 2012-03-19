package sparrow.elt.impl.writer;

import sparrow.elt.core.context.SparrowContext;
import sparrow.elt.core.util.GenericTokenResolver;

public class TestTokenResolver
    extends GenericTokenResolver {

  public static int counter = 1;


  public TestTokenResolver(SparrowContext context) {
    super.setContext(context);
  }

  public String getTokenValue(String token) {

    if ("currentmillisecond".equals(token)) {
      return String.valueOf(System.currentTimeMillis());
    }
    else if("seq_num".equals(token)){
      return String.valueOf(counter++);
    }
    else {
      return super.getTokenValue(token);
    }

  }

}
