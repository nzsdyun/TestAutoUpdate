package update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.sky.autoupdate.R;

/**
 * Automatic update module, you can use this class upgrade application
 * @author sky
 */
public class UpdateModule {
	private static UpdateModule sUpdateModule;
	/** @see AutoUpdate */
	private AutoUpdate mAutoUpdate;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == mAutoUpdate.getQuerySuccess()) {
				Context context = (Context) msg.obj;
				if (mAutoUpdate.isUpdate()) {
					showUpdateUI(mAutoUpdate.getUpdateInfo(), context);
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
		mAutoUpdate = new AutoUpdate(context, serverUrl, mHandler);
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
								// download
								downloadApk(context, updateInfo.getApkUrl());
							}
						}).create();
		updateDialog.setCancelable(false);
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.show();
	}
	
	private void downloadApk(Context context, String downloadUrl) {
		Intent intent = new Intent(context.getApplicationContext(),
				DownloadService.class);
		intent.putExtra("download_url", downloadUrl);
		context.startService(intent);
	}
}
