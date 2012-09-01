package sparrow.etl.core.dao.provider.impl;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */

public class CSVReader {
  /**
   * Constructor
   *
   * @param r input Reader source of CSV Fields to read.
   * @param separator
   * field separator character, usually ',' in North America,
   * ';' in Europe and sometimes '\t' for tab.
   */

  private LineNumberReader r;

  private String lastProcessedLine;

  private final boolean trimValue;

  CSVReader(Reader r1, char separator, boolean trimValue) {

    this.r = new LineNumberReader(r1);
    this.trimValue = trimValue;
    this.separator = separator;
  } // end of CSVReader

  /**
   * Constructor with default field separator ','.
   *
   * @param r input Reader source of CSV Fields to read.
   */
  CSVReader(Reader r1) {
    /* convert Reader to BufferedReader if necessary */
    this.r = new LineNumberReader(r1);
    this.separator = '@';
    this.trimValue = false;
  } // end of CSVReader

  /**
   *
   * @param r1 Reader
   * @param trimValue boolean
   */
  CSVReader(Reader r1, boolean trimValue) {
    /* convert Reader to BufferedReader if necessary */
    this.r = new LineNumberReader(r1);
    this.separator = '@';
    this.trimValue = trimValue;
  } // end of CSVReader

  private static final boolean debugging = true;

  /**
   * Reader source of the CSV fields to be read.
   */
  // private BufferedReader r;

  /*
   * field separator character, usually ',' in North America,
   * ';' in Europe and sometimes '\t' for tab.
   */
  private char separator;

  /**
   * category of end of line char.
   */
  private static final int EOL = 0;

  /**
   * category of ordinary character
   */
  private static final int ORDINARY = 1;

  /**
   * categotory of the quote mark "
   */
  private static final int QUOTE = 2;

  /**
   * category of the separator, e.g. comma, semicolon
   * or tab.
   */
  private static final int SEPARATOR = 3;

  /**
   * category of characters treated as white space.
   */
  private static final int WHITESPACE = 4;

  /**
   *
   * @throws Exception
   */
  public void reset() throws Exception {
    r.reset();
  }

  /**
   * categorise a character for the finite state machine.
   *
   * @param c the character to categorise
   * @return integer representing the character's category.
   */
  private int categorise(char c) {
    switch (c) {
      case ' ':
      case '\r':
      case 0xff:
        return WHITESPACE;
        // case ';':
        // case '!':
        //case '#':
        //return EOL;
      case '\n':
        return EOL; /* artificially applied to end of line */
      case '\"':
        return QUOTE;
      default:
        if (c == separator) {
          /* dynamically determined so can't use as case label */
          return SEPARATOR;
        }
        else if ('!' <= c && c <= '~') {
          /* do our tests in crafted order, hoping for an early return */
          return ORDINARY;
        }
        else if (0x00 <= c && c <= 0x20) {
          return WHITESPACE;
        }
        else if (Character.isWhitespace(c)) {
          return WHITESPACE;
        }
        else {
          return ORDINARY;
        }
    } // end of switch
  } // end of categorise

  /**
   * parser: We are in blanks before the field.
   */
  private static final int SEEKINGSTART = 0;

  /**
   * parser: We are in the middle of an ordinary field.
   */
  private static final int INPLAIN = 1;

  /**
   * parser: e are in middle of field surrounded in quotes.
   */
  private static final int INQUOTED = 2;

  /**
   * parser: We have just hit a quote, might be doubled
   * or might be last one.
   */
  private static final int AFTERENDQUOTE = 3;

  /**
   * parser: We are in blanks after the field looking for the separator
   */
  private static final int SKIPPINGTAIL = 4;

  /**
   * state of the parser's finite state automaton.
   */

  /**
   * The line we are parsing.
   * null means none read yet.
   * Line contains unprocessed chars. Processed ones are removed.
   */
  protected String line = null;

  /**
   * How many lines we have read so far.
   * Used in error messages.
   */
  private int lineCount = 0;

  /**
   *
   * @param headerLineNum int
   * @param numberOfColumn int
   * @throws IOException
   * @return String[]
   */
  public String[] getHeader(int headerLineNum, int numberOfColumn) throws
      IOException {

    int currentLineNumber = r.getLineNumber();
    String[] a = null;
    if (headerLineNum == -1) {
      a = new String[numberOfColumn];
      for (int i = 0; i < a.length; i++) {
        a[i] = "column_" + i;
      }
    }
    else {
      if (currentLineNumber < headerLineNum) {
        for (int i = currentLineNumber; i < headerLineNum - 1; i++) {
          r.readLine();
        }
      }
      a = getLine();
    }

    return a;
  }

  /**
   *
   * @param startPoint int
   */
  public void rollToStartingPoint(int startPoint) {
    int currentLineNumber = r.getLineNumber();
    try {
      if (currentLineNumber < startPoint) {
        for (int i = currentLineNumber; i < startPoint - 1; i++) {
          r.readLine();
        }
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   *
   * @return String
   */
  public String getLastProcessedLine() {
    return lastProcessedLine;
  }

  /**
   *
   * @return int
   */
  public int getLineNumber() {
    return lineCount;
  }

  /**
   *
   * @return String[]
   */
  public String[] getLine() {
    ArrayList lineArray = new ArrayList();
    String token = null;
    String returnArray[] = null;

    // reading values from line until null comes

    try {
      while (lineArray.size() == 0) {
        while ( (token = get()) != null) {

          if (token != null && trimValue) {
            token = token.trim();
          }
          //System.out.println("-->" + token);
          lineArray.add(token);
        } // end of while
      } // end of while
    }
    catch (EOFException e) {
      return null;
    }
    catch (IOException e) {
    }

    returnArray = new String[lineArray.size()];

// end of for

    return (String[]) lineArray.toArray(returnArray);
  }

  /**
   * Read one field from the CSV file
   *
   * @return String value, even if the field is numeric. Surrounded
   * and embedded double quotes are stripped.
   * possibly "". null means end of line.
   *
   * @exception EOFException
   * at end of file after all the fields have
   * been read.
   *
   * @exception IOException
   * Some problem reading the file, possibly malformed data.
   */
  protected String get() throws EOFException, IOException {
    StringBuffer field = new StringBuffer(50);
    /* we implement the parser as a finite state automaton with five states. */
    readLine();

    int state = SEEKINGSTART;
    /* start seeking, even if partway through a line */
    /* don't need to maintain state between fields. */

    /* loop for each char in the line to find a field */
    /* guaranteed to leave early by hitting EOL */
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      int category = categorise(c);

      switch (state) {
        case SEEKINGSTART: {
          /* in blanks before field */
          switch (category) {
            case WHITESPACE:
              field.append(c);
              state = INPLAIN;
              break;
            case QUOTE:
              field.append(c);
              state = INQUOTED;
              break;
              /**state = INQUOTED;
                             break;**/
            case SEPARATOR:

              /* end of empty field */
              line = line.substring(i + 1);
              return "";
            case EOL:

              /* end of line */
              line = null;
              return null;
            case ORDINARY:
              field.append(c);
              state = INPLAIN;
              break;
          }
          break;
        } // end of SEEKINGSTART
        case INPLAIN: {
          /* in middle of ordinary field */
          switch (category) {
            case QUOTE:
              field.append(c);
              break;
            case SEPARATOR:

              /* done */
              line = line.substring(i + 1);
              return field.toString().trim();
            case EOL:
              line = line.substring(i); /* push EOL back */
              return field.toString().trim();
            case WHITESPACE:
              field.append(' ');
              break;
            case ORDINARY:
              field.append(c);
              break;
          }
          break;
        } // end of INPLAIN
        case INQUOTED: {
          /* in middle of field surrounded in quotes */
          switch (category) {
            case QUOTE:
              field.append(c);
              state = AFTERENDQUOTE;
              break;
            case EOL:
              line = null;
              readLine();
              i = -1;
              field.append('\n');
              break;
            case WHITESPACE:
              field.append(' ');
              break;
            case SEPARATOR:
            case ORDINARY:
              field.append(c);
              break;
          }
          break;
        } // end of INQUOTED
        case AFTERENDQUOTE: {
          /* In situation like this "xxx" which may
             turn out to be xxx""xxx" or "xxx",
             We find out here. */
          switch (category) {
            case QUOTE:
              field.append(c);
              state = INQUOTED;
              break;
            case SEPARATOR:

              /* we are done.*/
              line = line.substring(i + 1);
              return field.toString().trim();
            case EOL:
              line = line.substring(i); /* push back eol */
              return field.toString().trim();
            case WHITESPACE:

              /* ignore trailing spaces up to separator */
              state = SKIPPINGTAIL;
              break;
            case ORDINARY:
              throw new IOException(
                  "Malformed CSV stream, missing separator after field on line " +
                  lineCount);
          }
          break;
        } // end of AFTERENDQUOTE
        case SKIPPINGTAIL: {
          /* in spaces after field seeking separator */
          switch (category) {
            case SEPARATOR:

              /* we are done.*/
              line = line.substring(i + 1);
              return field.toString().trim();
            case EOL:
              line = line.substring(i); /* push back eol */
              return field.toString().trim();
            case WHITESPACE:

              /* ignore trailing spaces up to separator */
              break;
            case QUOTE:
            case ORDINARY:
              throw new IOException(
                  "Malformed CSV stream, missing separator after field on line " +
                  lineCount);
          } // end of switch
          break;
        } // end of SKIPPINGTAIL
      } // end switch(state)
    } // end for
    throw new IOException(
        "Program logic bug. Should not reach here. Processing line " +
        lineCount);
  } // end get

  /**
   * Make sure a line is available for parsing.
   * Does nothing if there already is one.
   *
   * @exception EOFException
   */
  protected void readLine() throws EOFException, IOException {
    if (line == null) {
      line = r.readLine(); /* this strips platform specific line ending */
      lastProcessedLine = line;

      if (line != null) { //To handle the condition where the line ends with ",\n"
        int lastSeparatorPos = line.lastIndexOf(separator);
        if ( (lastSeparatorPos + 1) == line.length()) {
          line = line.substring(0, line.length() - 1) + separator + " \n";
        }
      }

      if (line == null) {
        /* null means EOF, yet another inconsistent Java convention. */
        throw new EOFException();
      }
      else {
        line += '\n'; /* apply standard line end for parser to find */
        lineCount++;
      }
    }
  } // end of readLine

  /**
   * Skip over fields you don't want to process.
   *
   * @param fields How many field you want to bypass reading.
   * The newline counts as one field.
   * @exception EOFException
   * at end of file after all the fields have
   * been read.
   * @exception IOException
   * Some problem reading the file, possibly malformed data.
   */
  public void skip(int fields) throws EOFException, IOException {
    if (fields <= 0) {
      return;
    }
    for (int i = 0; i < fields; i++) {
      // throw results away
      get();
    }
  } // end of skip

  /**
   * Skip over remaining fields on this line you don't want to process.
   *
   * @exception EOFException
   * at end of file after all the fields have
   * been read.
   * @exception IOException
   * Some problem reading the file, possibly malformed data.
   */
  public void skipToNextLine() throws EOFException, IOException {
    if (line == null) {
      readLine();
    }
    line = null;
  } // end of skipToNextLine

  /**
   * Close the Reader.
   */
  public void close() throws IOException {
    if (r != null) {
      r.close();
      r = null;
    }
  } // end of close

  /**
   * @param args [0]: The name of the file.
   */
  private static void testSingleTokens(String[] args) {
    if (debugging) {
      try {
        // read test file
        CSVReader csv = new CSVReader(new FileReader(args[0]), ',', true);
        try {
          while (true) {
            System.out.println(csv.get());
          }
        }
        catch (EOFException e) {
        }
        csv.close();
      }
      catch (IOException e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
      }
    } // end if
  } // end of testSingleTokens

  /**
   * @param args [0]: The name of the file.
   */
  private static void testLines(String[] args) {
    int lineCounter = 0;
    String loadLine[] = null;
    String DEL = ",";

    if (debugging) {
      try {
        // read test file
        CSVReader csv = new CSVReader(new FileReader(args[0]), '|', true);
        //    System.out.println(csv.getHeader(2));

        while ( (loadLine = csv.getLine()) != null) {
          lineCounter++;
          StringBuffer logBuffer = new StringBuffer();
          String logLine;
          //log.debug("#" + lineCounter +" : '" + loadLine.length + "'");
          logBuffer.append(loadLine[0]); // write first token, then write DEL in loop and the whole rest.
          for (int i = 1; i < loadLine.length; i++) {
            logBuffer.append(DEL).append(loadLine[i]);
          }
          logLine = logBuffer.toString();
          logLine.substring(0, logLine.lastIndexOf(DEL));
          //logLine.delete(logLine.lastIndexOf(DEL), logLine.length()); // is supported since JDK 1.4
          //System.out.println("#" + lineCounter +" : '" + loadLine.length + "' " + logLine);
          System.out.println(logLine);
        } // end of while
        csv.close();
      }
      catch (IOException e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
      }
    } // end if
  } // end of testLines

  /**
   * Test driver
   *
   * @param args [0]: The name of the file.
   */
  static public void main(String[] args) {
    //testSingleTokens(args);

    testLines(new String[] {"c:/app/DB-FOBOCA/samples/pipeSaperated.csv"});
  } // end main
} // end CSVReader

// end of file
