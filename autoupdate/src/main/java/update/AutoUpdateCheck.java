package update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;

import com.example.sky.autoupdate.R;

import http.DefaultHttpRequest;
import http.IHttpRequest;
import http.ResponseListenerAdapter;

/**
 * Automatic update detection module
 * @author sky
 */
public class AutoUpdateCheck {
	private static final int QUERY_SUCCESS = 1;
	private static final int QUERY_SUCCESS_AUTO = 2;
	private static final String TAG = AutoUpdateCheck.class.getSimpleName();
	private int mLocalVersion = -1;
	/** http request interface,default use HttpUrlConnection */
	private IHttpRequest mHttpRequest;
	/** server update information */
	private UpdateInfo mUpdateInfo;
	private Context mContext;
	/** show progress dialog */
	private ProgressDialog mProgressDialog;

	public AutoUpdateCheck(Context context) {
		try {
			this.mContext = context;
			mLocalVersion = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			//use default http request
			mHttpRequest = new DefaultHttpRequest(context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void check(String serverUrl, final Handler handler, boolean showProgressDialog) {
		check(serverUrl, handler, showProgressDialog, getQuerySuccess());
	}

	public void check(String serverUrl, final Handler handler, boolean showProgressDialog, final int querySuccessFlag) {
		//FIXME: add request check dialog
		if (showProgressDialog) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getString(R.string.android_auto_update_dialog_checking));
			mProgressDialog.show();
		}
		//FIXME:use http get an asynchronous request
		mHttpRequest.asyncGet(serverUrl, new ResponseListenerAdapter() {

			@Override
			public void success(String content) {
				Log.i("AutoUpdateCheck", "success:" + content);
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				mUpdateInfo = ParseHandler.parseInfo(content);
				handler.sendMessageDelayed(handler.obtainMessage(querySuccessFlag, mContext), 500);
			}

			@Override
			public void error(String cause) {
				Log.i("AutoUpdateCheck", "error cause:" + cause);
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				super.error(cause);
			}
		});

	}
	/**
	 * interpretation if need to update
	 * @return If the current version number less than 
	 * the version number of the server then returns true, otherwise false
	 */
	public synchronized boolean isUpdate() {
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
	/** get query success */
	public int getQuerySuccess() {
		return QUERY_SUCCESS;
	}
	/** automatic mode query success flag */
	public int getQuerySuccessAuto() {
		return QUERY_SUCCESS_AUTO;
	}

}
