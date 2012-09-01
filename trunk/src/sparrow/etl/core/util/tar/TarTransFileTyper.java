package sparrow.etl.core.util.tar;

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
public class
    TarTransFileTyper {
  /**
   * Return true if the file should be translated as ASCII.
   *
   * @param f The file to be checked to see if it need ASCII translation.
   */

  public boolean
      isAsciiFile(File f) {
    return false;
  }

  /**
   * Return true if the file should be translated as ASCII based on its name.
   * The file DOES NOT EXIST. This is called during extract, so all we know
   * is the file name.
   *
   * @param name The name of the file to be checked to see if it need ASCII
   *        translation.
   */

  public boolean
      isAsciiFile(String name) {
    return false;
  }

}
