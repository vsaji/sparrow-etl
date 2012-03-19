package sparrow.elt.impl.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.ResourceException;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.resource.SCPClientSession;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SCPWriter extends FTPWriter{

  public SCPWriter(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   *
   */
  public void doEndOfProcess() {
    SCPClientSession scp = null;

    try {
      scp = (SCPClientSession) config.getContext().getResource(resource).
          getResource();
    }
    catch (ResourceException ex) {
      throw new SparrowRuntimeException(ex);
    }
    scp.open();

    for (int i = 0; i < files.length; i++) {
      String dest = destDir+"/"+files[i].substring(files[i].lastIndexOf("/")+1);
      scp.upload(files[i],dest);
    }
    scp.close();
  }

}
