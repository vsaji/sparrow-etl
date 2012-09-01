package sparrow.etl.core.util;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
public class AsyncRequest {

  final RequestListener requestListener;
  final Object request;

  /**
   *
   * @param requestFor String
   * @param request Object
   */
  public AsyncRequest(RequestListener requestListener, Object request) {
    this.requestListener = requestListener;
    this.request = request;
  }

  public Object getRequest() {
    return request;
  }

  public RequestListener getRequestListener() {
    return requestListener;
  }

}
