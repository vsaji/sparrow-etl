package sparrow.etl.core.dao.impl;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DisconnectedRecordSet extends RecordSetImpl_Disconnected{

  public DisconnectedRecordSet() {
      this(null);
    }

    public DisconnectedRecordSet(ResultRow row) {
      super(row);
    }

}
