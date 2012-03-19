package sparrow.elt.core.dao.impl;

public abstract interface PostProcessAcknowledgement {

  public static final int SUCCESS = 2;
  public static final int IGNORED = 3;
  public static final int FAILED = -1;

  public abstract void acknowledge(int flag);

}
