package sparrow.elt.core.dao.impl;

import java.util.Date;

import sparrow.elt.core.exception.DataException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public interface ResultRow {

  abstract String getColumnName(int index) throws DataException;

  abstract String getValue(String fieldName) throws DataException;

  abstract String getValue(int index) throws DataException;

  abstract Number getNumber(String fieldName) throws DataException;

  abstract Number getNumber(int index) throws DataException;

  abstract int getColumnCount() throws DataException;

  abstract Integer getInt(String fieldName)throws DataException;

  abstract Integer getInt(int index) throws DataException;

  abstract Object getObject(String fieldName) throws DataException;

  abstract Object getObject(int index) throws DataException;

  abstract Double getDouble(int index) throws DataException;

  abstract Double getDouble(String fieldName) throws DataException;

  abstract Float getFloat(int index) throws DataException;

  abstract Float getFloat(String fieldName)throws DataException;

  abstract Long getLong(String fieldName)throws DataException;

  abstract Long getLong(int index) throws DataException;

  abstract byte[] getBlob(int index) throws DataException;

  abstract byte[] getBlob(String fieldName)throws DataException;

  abstract Date getDate(int index) throws DataException;

  abstract Date getDate(String fieldName) throws DataException;

  abstract void destroy();

  abstract ColumnHeader getHeader();

  abstract Object getChunk();

  abstract Object[] getValues();
  
  abstract String getValues(String[] keys,String separator) throws DataException;
  
  abstract long getRowNumber();

  abstract PostProcessAcknowledgement getPostProcessAcknowledgement();
}
