package update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.sky.autoupdate.R;

import util.AutoCheckUpdateReceiver;
import util.AutoCheckUpdateService;
import util.NetworkUtils;
import util.SharedPreferecesUtil;

/**
 * Automatic update module, you can use this class upgrade application,
 * support manual and automatic mode, when you use the automatic mode, you need to use the following code
 * <pre>
 * 	   //add follow code on the onResume() method in your main activity:
 * 	   <code>
 *     		@Override
 *     		protected void onResume() {
 *     			super.onResume();
 *     			UpdateModuleConfiguration updateModuleConfiguration = new UpdateModuleConfiguration.Builder(this)
 *     					.setServerConfigurationUrl(UPDATE_URL)
 *     					.setCheckUpdateIntervalTime(8 * 60 * 60 * 1000)
 *     					.setMaxCheckUpdateIntervalTime(7 * 24 * 60 * 60 * 1000)
 *     					.build();
 *     			UpdateModule.getInstance().startAutoCheckUpdate(updateModuleConfiguration);
 *     		}
 *     	</code>
 * </pre>
 * when you use manual mode, you can use the following code:
 * <pre>
 *     <code>
 *          UpdateModule.getInstance().checkUpdate(this, UPDATE_URL);
 *     </code>
 * </pre>
 *
 * @author sky
 */
public class UpdateModule {
	private static UpdateModule sUpdateModule;
	/** @see AutoUpdateCheck */
	private AutoUpdateCheck mAutoUpdateCheck;
	/** automatic mode configuration */
	private UpdateModuleConfiguration mUpdateModuleConfiguration;
	/** auto check update receiver */
	private AutoCheckUpdateReceiver mAutoCheckUpdateReceiver;
	/** whether show dialog */
	private AtomicBoolean mShowDialog = new AtomicBoolean(false);
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == mAutoUpdateCheck.getQuerySuccess()) {
				Context context = (Context) msg.obj;
				if (mAutoUpdateCheck.isUpdate()) {
					if (!isShowDialog()) {
						mShowDialog.set(true);
						showUpdateUI(mAutoUpdateCheck.getUpdateInfo(), context);
					}
				} else {
					Toast.makeText(context, R.string.android_auto_update_toast_no_new_update, Toast.LENGTH_LONG)
							.show();
				}
			} else if (msg.what == mAutoUpdateCheck.getQuerySuccessAuto()) {
				Context context = (Context) msg.obj;
				if (mAutoUpdateCheck.isUpdate()) {
					if (!isShowDialog()) {
						mShowDialog.set(true);
						showUpdateUI(mAutoUpdateCheck.getUpdateInfo(), context);
					}
				} else {
					//TODO: increase check interval time for reduce 
					SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
		                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, System.currentTimeMillis());
					AutoCheckUpdateService.setCheckIntervalTimes(context, mAutoUpdateCheck.getUpdateInfo());
				}
			}
			super.handleMessage(msg);
		}
	};

	public static UpdateModule getInstance() {
		if (sUpdateModule == null) {
			synchronized (UpdateModule.class) {
				if (sUpdateModule == null) {
					sUpdateModule = new UpdateModule();
				}
			}
		}
		return sUpdateModule;
	}
	/**
	 * to check whether the updated in every period of time 
	 * @param context @see Context 
	 * @param serverUrl check update server url
	 * @param checkIntervalTime check update interval time, Unit of milliseconds
	 */
	public void checkUpdate(Context context, String serverUrl, long checkIntervalTime) {
//		long localIntervalTime = SharedPreferecesUtil.getLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME, 
//				UpdateModuleConfiguration.CHECK_INTERVAL_TIME_STRING, 0);
        long lastUpdateTime = SharedPreferecesUtil.getLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, 0);
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdateTime >= checkIntervalTime) {
			checkUpdate(context, serverUrl, false);
            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, currentTime);
		}
	}

	public void checkUpdate(Context context, String serverUrl) {
		checkUpdate(context, serverUrl, true);
	}

	public void checkUpdate(Context context, String serverUrl, boolean showProgressDialog) {
		mAutoUpdateCheck = new AutoUpdateCheck(context);
		if (mAutoUpdateCheck != null) {
			mAutoUpdateCheck.check(serverUrl, mHandler, showProgressDialog);
		}
	}

	/** automatic mode, you need to init UpdateModuleConfiguration and call in the main activity onCreate() method */
	public void startAutoCheckUpdate(UpdateModuleConfiguration updateModuleConfiguration) {
		this.mUpdateModuleConfiguration = updateModuleConfiguration;
//		//register broadcast
//		mUpdateModuleConfiguration.mContext.registerReceiver(
//				mAutoCheckUpdateReceiver = new AutoCheckUpdateReceiver(), new IntentFilter());
		if (isMeetUpdate(updateModuleConfiguration)) {
			AutoCheckUpdateService.startService(updateModuleConfiguration.mContext);
		}
	}
	/** automatic mode, you need to call in the main activity onDestroy() method */
	public void stopAutoCheckUpdate() {
//		if (mAutoCheckUpdateReceiver != null) {
//			mUpdateModuleConfiguration.mContext.unregisterReceiver(mAutoCheckUpdateReceiver);
//		}
//		mAutoCheckUpdateReceiver = null;
		mUpdateModuleConfiguration = null;
		mAutoUpdateCheck = null;
		mShowDialog.set(false);
	}
	/**automatic mode, when update interval time come back will call this method */
	public void autoCheckUpdate() {
		if (mUpdateModuleConfiguration != null) {
			mAutoUpdateCheck = new AutoUpdateCheck(mUpdateModuleConfiguration.mContext);
			if (mAutoUpdateCheck != null) {
				mAutoUpdateCheck.check(mUpdateModuleConfiguration.mServerConfigurationUrl, mHandler,
						false, mAutoUpdateCheck.getQuerySuccessAuto());
			}
		}
	}

	/** current activity whether destroy */
	public boolean isDestroy() {
		return mUpdateModuleConfiguration == null
				|| mAutoUpdateCheck == null;
	}
	/** whether show dialog */
	public boolean isShowDialog() {
		return mShowDialog.get();
	}

	public long checkUpdateIntervalTime() {
		if (mUpdateModuleConfiguration != null) {
			return mUpdateModuleConfiguration.mCheckUpdateIntervalTime;
		}
		return 8 * 60 * 60 * 1000;
	}

	public long checkMaxUpdateIntervalTime() {
		if (mUpdateModuleConfiguration != null) {
			return mUpdateModuleConfiguration.mMaxCheckUpdateIntervalTime;
		}
		return 7 * 24 * 60 * 60 * 1000;
	}
	/** the number of minutes to start checking the time */
	public long startCheckTime() {
		if (mUpdateModuleConfiguration != null) {
			return mUpdateModuleConfiguration.mStartCheckTime;
		}
		return 10 * 60;
	}
	/** the number of minutes to end checking the time */
	public long endCheckTime() {
		if (mUpdateModuleConfiguration != null) {
			return mUpdateModuleConfiguration.mEndCheckTime;
		}
		return 11 * 60;
	}
	
	/**
	 * show update ui
	 * @param updateInfo @see UpdateInfo
	 */
	private void showUpdateUI(final UpdateInfo updateInfo, final Context context) {
		AlertDialog updateDialog = new AlertDialog.Builder(context)
				.setTitle(updateInfo.getUpdateTips())
				.setMessage(updateInfo.getUpdateContent())
				.setNegativeButton(R.string.android_auto_update_dialog_btn_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								mShowDialog.set(false);
					            //FIXME: save the current time and calculate the update calculation interval
					            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
					                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, System.currentTimeMillis());
					            AutoCheckUpdateService.retryUpdateIntervalTime(context);
							}
						})
				.setPositiveButton(R.string.android_auto_update_dialog_btn_download,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								NetworkUtils.NetworkType networkType =  NetworkUtils.getNetworkType(context);
								if (networkType == NetworkUtils.NetworkType.OTHER) {
									showMobileNetDialog(updateInfo, context);
								} else if (networkType == NetworkUtils.NetworkType.WIFI) {
									// download
									downloadApk(context, updateInfo.getApkUrl(), updateInfo.isDebugVersion());
									mShowDialog.set(false);
						            //FIXME: save the current time and calculate the update calculation interval
						            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
						                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, System.currentTimeMillis());
						            AutoCheckUpdateService.retryUpdateIntervalTime(context);
								}
							}
						}).create();
		updateDialog.setCancelable(false);
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.show();
	}
	
	/**
	 * check whether to meet the check update conditions
	 * @param configuration @see UpdateModuleConfiguration
	 * @return if meet return true, otherwise return false.
	 */
	private boolean isMeetUpdate(final UpdateModuleConfiguration configuration) {
		if (configuration != null) {
			long startCheckTime = configuration.mStartCheckTime;
			long endCheckTime = configuration.mEndCheckTime;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			String[] times = simpleDateFormat.format(new Date()).split(":");
			long hour = Long.parseLong(times[0]);
			long time = Long.parseLong(times[1]);
			long currenMinute = hour * 60 + time;
			Log.i("AutoCheckUpdateService", "currenMinute:" + currenMinute + "startCheckTime:" 
					+ startCheckTime + ", endCheckTime:" + endCheckTime);
			if (currenMinute >= startCheckTime 
					&& currenMinute <= endCheckTime) {
				return true;
			}
		}
		return false;
	}

	/**
	 * show warm dialog when network is not wifi
	 * @param updateInfo @see UpdateInfo
	 * @param context @see Context
     */
	private void showMobileNetDialog(final UpdateInfo updateInfo, final Context context) {
		AlertDialog mobileNetDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.android_auto_update_mobile_dialog_tips)
				.setMessage(R.string.android_auto_update_mobile_dialog_content)
				.setNegativeButton(R.string.android_auto_update_mobile_dialog_btn_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								mShowDialog.set(false);
					            //FIXME: save the current time and calculate the update calculation interval
					            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
					                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, System.currentTimeMillis());
					            AutoCheckUpdateService.retryUpdateIntervalTime(context);
							}
						})
				.setPositiveButton(R.string.android_auto_update_mobile_dialog_btn_download,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								downloadApk(context, updateInfo.getApkUrl(), updateInfo.isDebugVersion());
								mShowDialog.set(false);
					            //FIXME: save the current time and calculate the update calculation interval
					            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
					                    UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, System.currentTimeMillis());
					            AutoCheckUpdateService.retryUpdateIntervalTime(context);
							}
						}).create();
		mobileNetDialog.setCancelable(false);
		mobileNetDialog.setCanceledOnTouchOutside(false);
		mobileNetDialog.show();
	}

	private void downloadApk(Context context, String downloadUrl, boolean isBugVersion) {
		// modify use system DownloadManager down apk
		Intent intent = new Intent(context.getApplicationContext(),
				DownloadManagerService.class);
		intent.putExtra("download_url", downloadUrl);
		intent.putExtra("debug_version", isBugVersion);
		context.startService(intent);
	}
}
