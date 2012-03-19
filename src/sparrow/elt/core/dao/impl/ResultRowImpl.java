package sparrow.elt.core.dao.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import sparrow.elt.core.dao.util.DBUtil;
import sparrow.elt.core.exception.DataException;


/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Saji Venugopalan
 * @version 1.0
 */
public class ResultRowImpl implements ResultRow {

	/**
	 * 
	 */
	private final ColumnHeader header;

	private Object[] row = null;

	private Object chunk = null;
	
	private long rowNumber = 0;

	private PostProcessAcknowledgement ppc = null;

	private static final String COLUMN_NOT_FOUND = "COLUMN_NOT_FOUND";

	private static final String UNKNOWN_EXCEPTION = "UNKNOWN_EXCEPTION";

	/**
	 * 
	 * @param header
	 *            ColumnHeaderVO
	 */
	public ResultRowImpl(ColumnHeader header) {
		this.header = header;
		this.row = new Object[header.getColumnCount()];
	}

	/**
	 * 
	 * @param header
	 *            ColumnHeader
	 * @param row
	 *            Object[]
	 */
	public ResultRowImpl(ColumnHeader header, Object[] row) {
		this.header = header;
		this.row = row;
		this.chunk = toString();
	}

	/**
	 * 
	 * @param header
	 *            ColumnHeader
	 * @param row
	 *            Object[]
	 * @param chunk
	 *            Object
	 */
	public ResultRowImpl(ColumnHeader header, Object[] row, Object chunk,long rowNumber) {
		this.header = header;
		this.row = row;
		this.chunk = chunk;
		this.rowNumber = rowNumber;
	}

	/**
	 * 
	 * @param header
	 *            ColumnHeader
	 * @param row
	 *            Object[]
	 * @param chunk
	 *            Object
	 */
	public ResultRowImpl(ColumnHeader header, Object[] row,
			PostProcessAcknowledgement ppc) {
		this.header = header;
		this.row = row;
		this.chunk = toString();
		this.ppc = ppc;
	}

	/**
	 * 
	 * @param rs
	 *            ResultSet
	 */
	public ResultRowImpl setResultSet(ResultSet rs) throws IOException,
			SQLException {

		int rsCount = 0;

		for (int i = 0; i < row.length; i++) {

			rsCount = i + 1;

			switch (header.getFieldType(i)) {
			case ColumnTypes.BLOB:
				row[i] = (rs.getBlob(rsCount) != null) ? DBUtil.readBytes(rs
						.getBlob(rsCount)) : null;
				break;
			case ColumnTypes.CLOB:
				row[i] = (rs.getClob(rsCount) != null) ? DBUtil.readBytes(rs
						.getClob(rsCount)) : null;
				break;
			case ColumnTypes.DATE:
				row[i] = (rs.getTimestamp(rsCount) == null) ? null : new Date(
						rs.getTimestamp(rsCount).getTime());
				break;
			default:
				row[i] = rs.getObject(rsCount);
				break;

			}
			if (row.length == rsCount) {
				break;
			}
		}
		chunk = toString();
		return this;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return String
	 */
	public String getColumnName(int index) {
		return header.getFieldName(index);
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return String
	 */
	public String getValue(String fieldName) throws DataException {
		String val;
		try {
			val = getValue(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return String
	 */
	public String getValue(int index) {
		return (row[index] == null) ? null : row[index].toString();
	}

	/**
	 * 
	 * @return int
	 */
	public int getColumnCount() {
		return row.length; // header.getColumnCount();
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return Integer
	 */
	public Integer getInt(String fieldName) throws DataException {
		Integer val;
		try {
			val = getInt(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return Integer
	 */
	public Integer getInt(int index) {
		Object r = row[index];
		return (r != null) ? Integer.valueOf(r.toString().trim()) : null;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return Double
	 */
	public Double getDouble(int index) {
		Object r = row[index];
		return (r != null) ? Double.valueOf(r.toString().trim()) : null;
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return Double
	 */
	public Double getDouble(String fieldName) throws DataException {
		Double val;
		try {
			val = getDouble(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return Long
	 */
	public Long getLong(String fieldName) throws DataException {
		Long val;
		try {
			val = getLong(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return Long
	 */
	public Long getLong(int index) {
		Object r = row[index];
		return (r != null) ? Long.valueOf(r.toString().trim()) : null;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return byte[]
	 */
	public byte[] getBlob(int index) {
		return (byte[]) row[index];
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return byte[]
	 */
	public byte[] getBlob(String fieldName) throws DataException {
		byte[] val;
		try {
			val = getBlob(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @param index
	 *            int
	 * @return Date
	 */
	public Date getDate(int index) {
		return (Date) row[index];
	}

	/**
	 * 
	 * @param fieldName
	 *            String
	 * @return Date
	 */
	public Date getDate(String fieldName) throws DataException {
		Date val;
		try {
			val = getDate(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < row.length; i++) {
			sb.append(row[i]).append(",");
		}
		sb = sb.deleteCharAt(sb.length() - 1);

		/***********************************************************************
		 * StringBuffer sb = new StringBuffer("<row>"); for (int i = 0; i <
		 * row.length; i++) { sb.append("<"+header.getFieldName(i)+">").append(row[i]).append("</"+header.getFieldName(i)+">").append("\n"); }
		 * sb.append("</row>");
		 **********************************************************************/

		return sb.toString();
	}

	/**
	 * destroy
	 */
	public void destroy() {
		this.row = null;
	}

	/**
	 * getHeader
	 * 
	 * @return ColumnHeader
	 */
	public ColumnHeader getHeader() {
		return header;
	}

	/**
	 * getFloat
	 * 
	 * @param index
	 *            int
	 * @return double
	 */
	public Float getFloat(int index) {
		Object r = row[index];
		return (r != null) ? Float.valueOf(r.toString()) : null;
	}

	/**
	 * getFloat
	 * 
	 * @param fieldName
	 *            String
	 * @return double
	 */
	public Float getFloat(String fieldName) throws DataException {
		Float val;
		try {
			val = getFloat(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]:" + e.getMessage());
		}
		return val;

	}

	/**
	 * getChunk
	 * 
	 * @return Object
	 */
	public Object getChunk() {
		return this.chunk;
	}

	/**
	 * getObject
	 * 
	 * @param fieldName
	 *            String
	 * @return Integer
	 */
	public Object getObject(String fieldName) throws DataException {

		Object val;
		try {
			val = getObject(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]");
		}
		return val;
	}

	/**
	 * getObject
	 * 
	 * @param index
	 *            int
	 * @return Integer
	 */
	public Object getObject(int index) {
		return row[index];
	}

	/**
	 * 
	 */

	public PostProcessAcknowledgement getPostProcessAcknowledgement() {
		return this.ppc;
	}

	/**
	 * getValues
	 * 
	 * @return Object[]
	 */
	public Object[] getValues() {
		return row;
	}

	/**
	 * getNumber
	 * 
	 * @param index
	 *            int
	 * @return Number
	 */
	public Number getNumber(int index) {
		Object r = row[index];
		return (r != null) ? (Number) r : null;
	}

	/**
	 * getNumber
	 * 
	 * @param fieldName
	 *            String
	 * @return Number
	 */
	public Number getNumber(String fieldName) throws DataException {
		Number val;
		try {
			val = getNumber(header.getFieldIndex(fieldName));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DataException(COLUMN_NOT_FOUND, "Column [" + fieldName
					+ "] not found");
		} catch (Exception e) {
			throw new DataException(UNKNOWN_EXCEPTION,
					"Exception occured while accessing Column [" + fieldName
							+ "]");
		}
		return val;

	}

	/**
	 * 
	 */
	public String getValues(String[] keys, String separator)
			throws DataException {
		if (keys != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < keys.length; i++) {
				String cntValue = getValue(keys[i]);
				cntValue = (cntValue!=null) ? cntValue.trim() : null;
				sb.append(cntValue).append(separator);
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
		return null;
	}

	/**
	 * 
	 */
	public long getRowNumber() {
		return rowNumber;
	}

}
