package sparrow.elt.impl.extractor.jms;


public interface MessageStore {

  public void persist(SparrowJMSMessage msg) throws Exception;

  public void remove(Object key) throws Exception;

  public void preserve(Object key);

  public MessageIterator iterator();

  public int size();

}
