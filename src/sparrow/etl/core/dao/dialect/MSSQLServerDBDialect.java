package sparrow.etl.core.dao.dialect;

import java.sql.Connection;
import java.sql.Types;

import sparrow.etl.core.dao.impl.ColumnTypes;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class MSSQLServerDBDialect
    extends SybaseDBDialect {


  public MSSQLServerDBDialect() {
  }


  /**
   * resolveSpearColumnType
   *
   * @param colType int
   * @param colScale int
   * @return int
   */
  public int resolveSpearColumnType(int colType, String colTypeName,
                                    int colScale) {
    switch (colType) {
      case Types.BINARY:
        if (colTypeName.equals("timestamp")) {
          return ColumnTypes.STRING;
        }
      default:
        return super.resolveSpearColumnType(colType, colTypeName, colScale);
    }
  }

}
