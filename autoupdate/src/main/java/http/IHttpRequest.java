package http;

import java.util.Map;
/**
 * the Http interface class, concrete by subclasses implementation
 * @author sky
 */
public interface IHttpRequest {
	/** request get method */
	String get(String url);
	/** async request get method */
	String asyncGet(String url, ResponseListener responceCallback);
	/** request get method include parametes */
	String get(String url, Map<String, String> params);
	/** async get method include parametes */
	String asyncGet(String url, Map<String, String> params, ResponseListener responceCallback);
	/** request post method */
	String post(String url);
	/** async request post method */
	String asyncPost(String url, ResponseListener responceCallback);
	/** request post method include parametes */
	String post(String url, Map<String, String> params);
	/** async request post method include parametes */
	String asyncPost(String url, Map<String, String> params, ResponseListener responceCallback);
}
