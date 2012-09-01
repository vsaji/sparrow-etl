package sparrow.etl.core.transformer;


import sparrow.etl.core.util.Constants;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class PlaceHolderFactory {
  public static final PlaceHolder resolvePlaceHolder(String placeHolder) {

    if (Constants.STRING.equals(placeHolder)) {
      return new StringPlaceHolder();
    }
    else if (Constants.MESSAGE.equals(placeHolder)) {
      return new MessagePlaceHolder();
    }
    else if (Constants.GLOBAL.equals(placeHolder)) {
      return new GlobalPlaceHolder();
    }
    else {
      return new ObjectPlaceHolder();
    }
  }
}
