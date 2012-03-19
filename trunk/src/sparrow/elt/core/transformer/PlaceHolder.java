package sparrow.elt.core.transformer;

import sparrow.elt.core.vo.DataOutputHolder;
import sparrow.elt.core.vo.MessageHolder;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface PlaceHolder {
  public void setValue(DataOutputHolder dh, String inputValueName, String key);
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class ObjectPlaceHolder
    implements PlaceHolder {
  public void setValue(DataOutputHolder dh, String inputValueName, String key) {
    dh.addObject(key, inputValueName);
  }
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class MessagePlaceHolder
    implements PlaceHolder {
  public void setValue(DataOutputHolder dh, String inputValueName, String key) {
    dh.addMessageHolder(key, new MessageHolder(inputValueName, null));
  }
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class StringPlaceHolder
    implements PlaceHolder {
  public void setValue(DataOutputHolder dh, String inputValueName, String key) {
    dh.addString(key, inputValueName);
  }
}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
class GlobalPlaceHolder
    implements PlaceHolder {
  public void setValue(DataOutputHolder dh, String inputValueName, String key) {
    dh.getTokenValue().put(key,inputValueName);
  }
}

