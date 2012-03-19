package sparrow.elt.core.resource;

import java.io.File;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public interface FTPClientSession {

  public static final int BINARY = 1;
  public static final int ASCII = 2;


  public void open();
  public void changeDirectory(String directory) ;
  public void upload(File file) ;
  public void changeMode(int mode);
  public void close();

}
