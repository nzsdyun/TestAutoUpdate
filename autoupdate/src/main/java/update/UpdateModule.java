package update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.sky.autoupdate.R;

import http.NetworkUtils;

/**
 * Automatic update module, you can use this class upgrade application
 * @author sky
 */
public class UpdateModule {
	private static UpdateModule sUpdateModule;
	/** @see AutoUpdateCheck */
	private AutoUpdateCheck mAutoUpdateCheck;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == mAutoUpdateCheck.getQuerySuccess()) {
				Context context = (Context) msg.obj;
				if (mAutoUpdateCheck.isUpdate()) {
					showUpdateUI(mAutoUpdateCheck.getUpdateInfo(), context);
				} else {
					Toast.makeText(context, R.string.android_auto_update_toast_no_new_update, Toast.LENGTH_LONG)
							.show();
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

	public void checkUpdate(Context context, String serverUrl) {
		checkUpdate(context, serverUrl, true);
	}

	public void checkUpdate(Context context, String serverUrl, boolean showProgressDialog) {
		mAutoUpdateCheck = new AutoUpdateCheck(context);
		if (mAutoUpdateCheck != null) {
			mAutoUpdateCheck.check(serverUrl, mHandler, showProgressDialog);
		}
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
									downloadApk(context, updateInfo.getApkUrl());
								}
							}
						}).create();
		updateDialog.setCancelable(false);
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.show();
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
							}
						})
				.setPositiveButton(R.string.android_auto_update_mobile_dialog_btn_download,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
								downloadApk(context, updateInfo.getApkUrl());
							}
						}).create();
		mobileNetDialog.setCancelable(false);
		mobileNetDialog.setCanceledOnTouchOutside(false);
		mobileNetDialog.show();
	}

	private void downloadApk(Context context, String downloadUrl) {
		Intent intent = new Intent(context.getApplicationContext(),
				DownloadService.class);
		intent.putExtra("download_url", downloadUrl);
		context.startService(intent);
	}
}
