package sparrow.etl.core.dao.metadata;

import sparrow.etl.core.util.Constants;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ColumnAttributes {

  private String columnName = null;
  private int size = 0;
  private String format = Constants.DEFAULT_DATE_FORMAT;
  private String defaultValue = null;
  private boolean excludeColumn = false;
  private String xPath = null;

  public String getFormat() {
    return format;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = (defaultValue != null) ?
        ( (defaultValue.trim().equals("")) ? null : defaultValue.trim()) : null;
  }

  public void setFormat(String format) {
    if (format != null && !format.trim().equals("")) {
      this.format = format;
    }
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setExcludeColumn(boolean excludeColumn) {
    this.excludeColumn = excludeColumn;
  }

  public void setXPath(String xPath) {
    this.xPath = xPath;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public int getSize() {
    return size;
  }

  public boolean isExcludeColumn() {
    return excludeColumn;
  }

  public String getXPath() {
    return xPath;
  }

  public ColumnAttributes() {
  }
}
