package sparrow.etl.impl.extractor.jms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import sparrow.etl.core.exception.InitializationException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FileBasedMessageStore
    implements MessageStore {

  final String filePath;
  final String preserveStore;
  private static int staticCounter = 0;
  private static final int nBits = 5;

  private static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(
      FileBasedMessageStore.class);

  static final String FILE_EXTN = ".sprmsg";

  /**
   *
   * @param filePath String
   * @param preserveStore String
   */
  public FileBasedMessageStore(String filePath, String preserveStore) {
    this.filePath = filePath;
    this.preserveStore = preserveStore;
    if (preserveStore != null && filePath.equals(preserveStore)) {
      throw new InitializationException("FILE_STORE_INIT_EXP",
          "File path cannot be same for [message.store.source] & [preserve.store]");
    }
  }

  /**
   * persist
   *
   * @param msg Message
   */
  public void persist(SparrowJMSMessage msg) throws Exception {

    //System.out.println(msg.getMessageId());
    String fileName = msg.getMessageId().hashCode() + "_" + getUnique();
    msg.setInternalMessageId(fileName);
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(
        filePath + "/" + fileName + FILE_EXTN));
    ObjectOutput output = new ObjectOutputStream(buffer);
    try {
      output.writeObject(msg);
    }
    finally {
      output.close();
    }
  }

  /**
   * size
   *
   * @return int
   */
  public int size() {
    return getMessageFiles().length;
  }

  /**
   * remove
   *
   * @param key Object
   */
  public void remove(Object key) {
    String msgKey = (String) key;
    new File(filePath + "/" + msgKey + FILE_EXTN).delete();
  }

  /**
   * iterator
   *
   * @return MessageIterator
   */
  public MessageIterator iterator() {
    return new FileMessageIteratorImpl();
  }

  /**
   *
   * @return File[]
   */
  private File[] getMessageFiles() {
    File[] file = new File(filePath).listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return (!pathname.isDirectory() &&
                pathname.getName().endsWith(FILE_EXTN));
      }
    });
    return file;
  }

  /**
   * preserve
   *
   * @param key Object
   */
  public void preserve(Object key) {

    if (preserveStore != null) {
      String fileName = key.toString() + FILE_EXTN;
      new File(filePath + "/" +
               fileName).renameTo(new File(preserveStore + "/" + fileName));
      logger.warn("Message [" + key + FILE_EXTN + "] has been moved to [" +
                  preserveStore + "/" + fileName + "]");
    }
  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  class FileMessageIteratorImpl
      implements MessageIterator {

    final File[] file;
    private int i = 0;

    FileMessageIteratorImpl() {
      file = getMessageFiles();
    }

    /**
     * hasNext
     *
     * @return boolean
     */
    public boolean hasNext() {
      return i < file.length;
    }

    /**
     * next
     *
     * @return Message
     */
    public SparrowJMSMessage next() {
      File f = file[i];
      SparrowJMSMessage message = null;
      try {
        InputStream buffer = new BufferedInputStream(new FileInputStream(f));
        ObjectInput input = new ObjectInputStream(buffer);
        message = ( (SparrowJMSMessage) input.readObject());
        message.setLoadedFromStore(true);
        input.close();
        buffer.close();
        i++;
      }
      catch (Exception e) {
        logger.error("Exception occured while read Message object from file [" +
                     f.getName() + "]", e);
      }
      return message;
    }

  }

  /**
   *
   * @return long
   */
  public static long getUnique() {
    return (System.currentTimeMillis() << nBits) |
        (staticCounter++ & 31 ^ nBits - 1);
  }

  /**
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    for (int i = 0; i < 20000; i++) {
      String a = System.currentTimeMillis() + "===" + getUnique();
      System.out.println(a);
    }

  }

}
