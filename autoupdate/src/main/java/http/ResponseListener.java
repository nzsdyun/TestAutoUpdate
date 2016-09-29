package http;
/**
 * the server response callback
 * @author sky
 */
public interface ResponseListener {
	/** Called when http responce success, Running on the UI thread */
	void success(String content);
	/** Called when http request error, Running on the UI thread */
	void error(String cause);
}
