package sparrow.etl.core.vo;

import sparrow.etl.core.dao.impl.RecordSet;

public class DataHolder {

  private RecordSet data = null;

  public void setData(RecordSet data) {
    this.data = data;
  }

  public RecordSet getData() {
    return data;
  }

  public DataHolder() {
  }

}
