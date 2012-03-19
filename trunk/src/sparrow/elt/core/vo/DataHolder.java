package sparrow.elt.core.vo;

import sparrow.elt.core.dao.impl.RecordSet;

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
