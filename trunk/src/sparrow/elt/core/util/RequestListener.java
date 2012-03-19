package sparrow.elt.core.util;

public interface RequestListener {

  public void process(Object o) throws Exception;

  public void endProcess();

}
