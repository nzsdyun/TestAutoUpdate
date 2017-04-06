package update;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**  server Content parser */
public class ParseHandler {
	private static final String TAG = ParseHandler.class.getSimpleName();
	/**
	 * parse json string, the json string need to comply with the following format
	 * <p><code>
	 *  {
	 *		"update_info": {
	 *			"version_code": 1,
	 *			"version_name": "emporia_launcher",
	 *			"apk_url": "http://www.emporia.com/apk/emporia_launcher.apk",
	 *			"update_content":"1. Bug fixes. \n 2. Increase the calling function",
	 *			"update_tips": "New feature updates",
	 *			"debug_version": false,
	 *			"check_number_times": 5
	 *		}  	
	 *  }
	 * </code></p>
	 * @param content server return content
	 * @return @see UpdateInfo
	 */
	public static UpdateInfo parseJsonInfo(String content) {
		UpdateInfo updateInfo = null;
		if (TextUtils.isEmpty(content))
			return null;
		try {
			JSONObject root = new JSONObject(content);
			JSONObject body = null;
			if ((body = root.getJSONObject("update_info")) != null) {
				updateInfo  = new UpdateInfo();
				updateInfo.setVersionCode(body.optInt("version_code"));
				updateInfo.setVersionName(body.optString("version_name"));
				updateInfo.setApkUrl(body.optString("apk_url"));
				updateInfo.setUpdateContent(body.optString("update_content"));
				updateInfo.setUpdateTips(body.optString("update_tips"));
				updateInfo.setDebugVersion(body.optBoolean("debug_version"));
				updateInfo.setCheckNumberOfTimes(body.optInt("check_number_times"));
			}
		} catch (JSONException e) {
			Log.e(TAG, "use json parse content failed:" + e.getMessage());
			return null;
		}
		return updateInfo;
	}
	/**
	 * parse xml string, the xml string need to comply with the following format
	 * <p><code>
	 * </code></p>
	 * @param content server return content
	 * @return @see UpdateInfo
	 */
	private static UpdateInfo parseXmlInfo(String content) {
		//TODO:temporary unrealized
		return null;
	}
	/**
	 * automatic field analytical judgment json or XML
	 * @param content parse string content
	 * @return parse success return UpdateInfo, otherwise return false
	 */
	public static UpdateInfo parseInfo(String content) {
		if (TextUtils.isEmpty(content))
			return null;
		if (isJson(content)) {
			return parseJsonInfo(content);
		} else if (isXml(content)) {
			return parseXmlInfo(content);
		} else {
			return null;
		}
	}
	private static boolean isXml(String content) {
		//TODO:temporary unrealized
		return false;
	}

	private static boolean isJson(String content) {
		try {
			new JSONObject(content);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
