package sparrow.etl.core.dao.util;

import java.sql.Types;

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
public class SPParamInfo {

  final int[] pos;
  final int[] type;
  final ColumnHeader columnHeader;
  final int paramLen;

  /**
   *
   * @param outParam String
   */
  public SPParamInfo(String outParam) {

    String[] totParam = outParam.split("[,]");
    paramLen = totParam.length;
    pos = new int[paramLen];
    type = new int[paramLen];
    int[] sprType = new int[paramLen];
    String[] colName = new String[paramLen];

    for (int i = 0; i < paramLen; i++) {
      String temp[] = totParam[i].split("[:]");
      pos[i] = Integer.parseInt(temp[0]);
      type[i] = DBUtil.getColumnType(Types.class, temp[1]);
      sprType[i] = DBUtil.resolveSparrowColumnType(type[i], 0);
      colName[i] = temp[2];
    }

    columnHeader = new ColumnHeader(colName, sprType);
  }

  public int getParamLen() {
    return paramLen;
  }

  public int[] getPos() {
    return pos;
  }

  public int[] getType() {
    return type;
  }

  public ColumnHeader getColumnHeader() {
    return columnHeader;
  }


}
