package http;

/**
 * Http request base configuration
 * @author sky
 */
public final class HttpConfiguration {
	/** http async request default thread pool size */
	public static final int DEFAULT_THREAD_SIZE = 5;
	/** http default connection time */
	public static final int DEFAULT_CONNECT_TIME = 10000;
	/** http default read time */
	public static final int DEFAULT_READ_TIME = 5000;
	/** http request encode */
	public static final String DEFAULT_REQUEST_ENCODE = "utf-8";
	/** http response result encode */
	public static final String DEFAULT_RESPONSE_ENCODE = "utf-8";
	/** http request get method */
	public static final String REQUEST_GET = "GET";
	/** http request post method */
	public static final String REQUEST_POST = "POST";
}
