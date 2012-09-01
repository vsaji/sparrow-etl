package sparrow.etl.impl.extractor.jms;

public class MessageStoreFactory {

  private MessageStoreFactory() {}

  static final MessageStore createMessageStore(String type, String source, String preseveStore) {
    if ("file".equals(type)) {
      return new FileBasedMessageStore(source,preseveStore);
    }
    else {
      return new FileBasedMessageStore(source,preseveStore);
    }
  }
}
