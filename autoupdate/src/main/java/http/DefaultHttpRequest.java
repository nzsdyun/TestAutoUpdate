package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * default use HttpUrlConnection request data, here are the get and post request
 * method of synchronous and asynchronous
 * @author sky
 */
public class DefaultHttpRequest implements IHttpRequest {
	private static final String TAG = DefaultHttpRequest.class.getSimpleName();
	/** the thread pool number */
	private static final int DEFAULT_THREAD_SIZE = HttpConfiguration.DEFAULT_THREAD_SIZE;
	/** use a single thread */
	private ExecutorService mRequestService = Executors.newFixedThreadPool(DEFAULT_THREAD_SIZE);
	/** @see Context */
	private Context mContext;
	/** http request encode */
	private String mRequestEncode = HttpConfiguration.DEFAULT_REQUEST_ENCODE;
	/** http response encode */
	private String mResponseEncode = HttpConfiguration.DEFAULT_RESPONSE_ENCODE;

	public DefaultHttpRequest(Context context) {
		this.mContext = context;
	}

	public DefaultHttpRequest(Context context, String requestEncode,
			String responseEncode) {
		this.mContext = context;
		if (!TextUtils.isEmpty(requestEncode)) {
			this.mRequestEncode = requestEncode;
		}
		if (!TextUtils.isEmpty(responseEncode)) {
			this.mResponseEncode = responseEncode;
		}
	}

	/** use string url get http url default configuration */
	private HttpURLConnection getDefaultConnection(String url,
			String requestMethod) throws IOException {
		return getDefaultConnection(new URL(url), requestMethod);
	}

	/** use URL url get http url default configuration */
	private HttpURLConnection getDefaultConnection(URL url, String requestMethod)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(HttpConfiguration.DEFAULT_CONNECT_TIME);
		connection.setReadTimeout(HttpConfiguration.DEFAULT_READ_TIME);
		connection.setRequestMethod(requestMethod);
		connection.setUseCaches(false);
		return connection;
	}

	/** send http request */
	private String sendHttpRequest(String url, final String requestMethod,
			final ResponseListener responseCallback) {
		return sendHttpRequest(url, requestMethod, responseCallback, null);
	}

	/** send http request, include parameters */
	private String sendHttpRequest(String url, final String requestMethod,
			final ResponseListener responseCallback, Map<String, String> params) {
		final StringBuffer result = new StringBuffer("");
		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			// FIXME: http get request
			if (requestMethod.equals(HttpConfiguration.REQUEST_GET)) {
				if (params != null && params.size() > 0) {
					String query = new StringBuffer("?").append(
							joinParams(params)).toString();
					url += URLEncoder.encode(query, mRequestEncode);
				}
			}
			HttpURLConnection connection = getDefaultConnection(url,
					requestMethod);
			// FIXME: http post request
			if (requestMethod.equals(HttpConfiguration.REQUEST_POST)) {
				if (params != null && params.size() > 0) {
					connection.setDoOutput(true);
					byte[] query = joinParams(params).getBytes();
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					connection.setRequestProperty("Content-Length",
							String.valueOf(query.length));
					outputStream = connection.getOutputStream();
					outputStream.write(query);
				}
			}
			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				inputStream = connection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					result.append(line);
				}
				responseSuccess(responseCallback, result.toString());
			} else {
				responseError(responseCallback, connection.getResponseMessage());
			}
			connection.disconnect();
		} catch (final MalformedURLException e) {
			Log.e(TAG, "http request failed:" + e.getMessage());
			responseError(responseCallback, e.getMessage());
			return null;
		} catch (final IOException e) {
			Log.e(TAG, "http request failed:" + e.getMessage());
			responseError(responseCallback, e.getMessage());
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	private String joinParams(Map<String, String> params) {
		StringBuffer query = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			query.append(key).append("=").append(value);
			query.append("&");
		}
		return query.deleteCharAt(query.length() - 1).toString();
	}

	private void asyncCall(final String url, final String requestMethod,
			final ResponseListener responseCallback) {
		asyncCall(url, requestMethod, responseCallback, null);
	}

	/**
	 * async call, use Executors
	 * @param url String url
	 * @param requestMethod http request method
	 * @param responseCallback http responce call back
	 * @param params request parameters
	 */
	private void asyncCall(final String url, final String requestMethod,
			final ResponseListener responseCallback,
			final Map<String, String> params) {
		mRequestService.execute(new Runnable() {
			@Override
			public void run() {
				sendHttpRequest(url, requestMethod,
								responseCallback, params);
			}
		});
	}

	/** the HTTP response callback can not correctly, which run on the UI thread */
	private void responseError(final ResponseListener responseCallback,
			final String error) {
		if (responseCallback != null) {
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					responseCallback.error(error);
				}
			});
		}
	}

	/** the HTTP response success callback, which run on the UI thread */
	private void responseSuccess(final ResponseListener responseCallback,
			final String result) {
		if (responseCallback != null) {
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						responseCallback.success(new String(result.getBytes(),
								mResponseEncode));
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "transcode failed:" + e.getMessage());
						responseCallback.success(result);
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public String get(String url) {
		return sendHttpRequest(url, HttpConfiguration.REQUEST_GET, null);
	}

	@Override
	public void asyncGet(String url, ResponseListener responseCallback) {
		asyncCall(url, HttpConfiguration.REQUEST_GET, responseCallback);
	}

	@Override
	public String get(String url, Map<String, String> params) {
		return sendHttpRequest(url, HttpConfiguration.REQUEST_GET, null, params);
	}

	@Override
	public void asyncGet(String url, Map<String, String> params,
			ResponseListener responseCallback) {
		asyncCall(url, HttpConfiguration.REQUEST_GET, responseCallback, params);
	}

	@Override
	public String post(String url) {
		return sendHttpRequest(url, HttpConfiguration.REQUEST_POST, null);
	}

	@Override
	public void asyncPost(String url, ResponseListener responseCallback) {
		asyncCall(url, HttpConfiguration.REQUEST_POST, responseCallback);
	}

	@Override
	public String post(String url, Map<String, String> params) {
		return sendHttpRequest(url, HttpConfiguration.REQUEST_POST, null,
				params);
	}

	@Override
	public void asyncPost(String url, Map<String, String> params,
			ResponseListener responseCallback) {
		asyncCall(url, HttpConfiguration.REQUEST_POST, responseCallback, params);
	}

}
