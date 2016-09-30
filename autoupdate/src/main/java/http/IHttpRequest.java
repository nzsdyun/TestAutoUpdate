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
	void asyncGet(String url, ResponseListener responceCallback);
	/** request get method include parametes */
	String get(String url, Map<String, String> params);
	/** async get method include parametes */
	void asyncGet(String url, Map<String, String> params, ResponseListener responceCallback);
	/** request post method */
	String post(String url);
	/** async request post method */
	void asyncPost(String url, ResponseListener responceCallback);
	/** request post method include parametes */
	String post(String url, Map<String, String> params);
	/** async request post method include parametes */
	void asyncPost(String url, Map<String, String> params, ResponseListener responceCallback);
}
