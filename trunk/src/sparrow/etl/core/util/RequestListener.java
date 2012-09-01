package sparrow.etl.core.util;

public interface RequestListener {

  public void process(Object o) throws Exception;

  public void endProcess();

}
