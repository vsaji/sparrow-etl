package sparrow.etl.impl.writer;

import sparrow.etl.core.config.SparrowDataWriterConfig;
import sparrow.etl.core.vo.DataOutputHolder;
import sparrow.etl.core.writer.AbstractDataWriter;

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
