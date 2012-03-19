package sparrow.elt.core.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class TransUtils {

  private static final Logger log = Logger.getLogger(TransUtils.class.getName());

  public TransUtils() {
  }

  public static boolean close(Connection conn) {
    if (conn != null) {
      try {
        conn.clearWarnings();
      }
      catch (SQLException se) {
        if (log.isDebugEnabled()) {
          log.debug("connection clear warnings failed " + se.getMessage());
        }
        return false;
      }

      try {
        conn.close();
      }
      catch (SQLException se) {
        se.printStackTrace();
        return false;
      }
    }

    return true;
  }
}
