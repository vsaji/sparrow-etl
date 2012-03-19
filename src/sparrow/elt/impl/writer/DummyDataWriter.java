package sparrow.elt.impl.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.writer.AbstractDataWriter;

public class DummyDataWriter
    extends AbstractDataWriter {
  public DummyDataWriter(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   * onError
   *
   * @param writerName String
   * @param ex Exception
   */
  public void onError(String writerName, Exception ex) {
  }

  /**
   * writeData
   *
   * @param data DataOutputHolder
   * @param statusCode int
   * @return int
   */
  public int writeData(DataOutputHolder data, int statusCode) {
    return 0;
  }

}
