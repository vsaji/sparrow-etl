package sparrow.etl.core.dao.provider.impl;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import sparrow.etl.core.dao.impl.ColumnHeader;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class FixedLengthReader
    extends CSVReader {

  final FixedLengthColumnResolver flcr;
  final int columnCount;
  final int[] columnLengths;

  private int currentColumn = 0;
  private int from = 0;
  private int nextCol = 0;

  /**
   *
   * @param r Reader
   * @param columnInfo String
   */
  FixedLengthReader(Reader r, ColumnHeader header, boolean trimValue) {
    super(r,trimValue);
    flcr = new FixedLengthColumnResolver(header);
    columnCount = flcr.getColumnCount();
    columnLengths = flcr.getColumnLengths();
  }

  /**
   *
   * @throws EOFException
   * @throws IOException
   * @return String
   */
  protected String get() throws EOFException, IOException {

    StringBuffer field = new StringBuffer(50);
    readLine();
    int curColLen = getColumnLength();
    nextCol += curColLen;

    for (int i = from; i < nextCol; i++) {
      char c = line.charAt(i);
      if (c == '\n') {
        resetVars();
        return null;
      }
      field.append(c);
    }

    from = nextCol;

    return field.toString();
  }

  /**
   *
   */
  private void resetVars() {
    nextCol = 0;
    from = 0;
    currentColumn = 0;
    line = null;

  }

  /**
   *
   * @return int
   */
  private int getColumnLength() {

    if (currentColumn >= columnCount) {
      currentColumn = 0;
    }

    int curCol = columnLengths[currentColumn];
    currentColumn++;

    return curCol;
  }

  /**
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    // read test file
    try {
      FixedLengthReader csv = new FixedLengthReader(new FileReader(
          "c:/app/DB-FOBOCA/samples/fixedLength.csv"),
          new ColumnHeader(new String[] {}),true);

      String[] loadLine = null;

      //    loadLine = csv.getHeader(2);
      for (int i = 0; i < loadLine.length; i++) {
        System.out.print(loadLine[i] + ",");
      }
      System.out.println();
      while ( (loadLine = csv.getLine()) != null) {
        for (int i = 0; i < loadLine.length; i++) {
          System.out.print(loadLine[i] + ",");
        }
        System.out.println();
      } // end of while
      csv.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  } // end if

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author Saji Venugopalan
   * @version 1.0
   */
  private class FixedLengthColumnResolver {

    final int[] columnLength;

    /**
     *
     * @param columnInfo String
     */
    FixedLengthColumnResolver(ColumnHeader header) {

      columnLength = new int[header.getColumnCount()];

      for (int i = 0; i < columnLength.length; i++) {
        columnLength[i] = header.getFieldSize(i);
      }
    }

    /**
     *
     * @return int
     */
    int getColumnCount() {
      return columnLength.length;
    }

    /**
     *
     * @param columnIndex int
     * @return int
     */
    int getColumnLength(int columnIndex) {
      return columnLength[columnIndex];
    }

    int[] getColumnLengths() {
      return columnLength;
    }

  }
}
