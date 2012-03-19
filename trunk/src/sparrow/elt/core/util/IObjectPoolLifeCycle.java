package sparrow.elt.core.util;

public interface IObjectPoolLifeCycle {

  abstract Object offered();

  abstract void returned(Object o);

  abstract void destroy();

}
