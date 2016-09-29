package update;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import http.DefaultHttpRequest;
import http.IHttpRequest;
import http.ResponseListenerAdapter;

/**
 * Automatic update detection module
 * @author sky
 */
public class AutoUpdate {
	private static final String TAG = AutoUpdate.class.getSimpleName();
	private int mLocalVersion = -1;
	/** http request interface,default use HttpUrlConnection */
	private IHttpRequest mHttpRequest;
	/** server update information */
	private UpdateInfo mUpdateInfo;

	public AutoUpdate(Context context, String serverUrl) {
		try {
			mLocalVersion = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			//use default http request
			mHttpRequest = new DefaultHttpRequest(context);
			//FIXME:use http get an asynchronous request
			mHttpRequest.asyncGet(serverUrl, new ResponseListenerAdapter() {

				@Override
				public void success(String content) {
					mUpdateInfo = ParseHandler.parseInfo(content);
				}
			});
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * interpretation if need to update
	 * @return If the current version number less than 
	 * the version number of the server then returns true, otherwise false
	 */
	public boolean isUpdate() {
		if (mUpdateInfo != null) {
			int serverVersionCode = mUpdateInfo.getVersionCode();
			if (serverVersionCode > 0 && mLocalVersion < serverVersionCode)
				return true;
		}
		return false;
	}
	/** get server update information */
	public UpdateInfo getUpdateInfo() {
		return mUpdateInfo;
	}

}
