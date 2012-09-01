package sparrow.etl.impl.extractor.jms;


public interface MessageIterator {

  public abstract boolean hasNext();

  public abstract SparrowJMSMessage next();

}
