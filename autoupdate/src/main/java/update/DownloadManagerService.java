package update;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.sky.autoupdate.R;

import java.io.File;
import java.io.IOException;

import util.SharedPreferecesUtil;

/**
 * use system DownloadManager down apk for download the stability
 * 
 * @author sky
 */
public class DownloadManagerService extends Service {

	private static final String TAG = DownloadService.class.getSimpleName();

	private DownloadManager downloadManager;
	private BroadcastReceiver downLoadBroadcast;
	private long downloadId;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			downloadApk(intent.getStringExtra("download_url"), 
					intent.getBooleanExtra("debug_version", false));
		}
		registerBroadcast();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterBroadcast();
	}

	private void downloadApk(String url, boolean isDebugVersion) {
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		// allow down's network type, now is wifi and mobile
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
				| DownloadManager.Request.NETWORK_MOBILE);
		// set notification
		request.setNotificationVisibility(
				DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setTitle(getString(R.string.android_auto_update_notification_title));
		request.setDescription(getString(
				R.string.android_auto_update_notification_content,
				getApplicationName()));
		// TODO: whether allow over roam down apk
		if (isDebugVersion) {
			request.setAllowedOverRoaming(true);
		} else {
			request.setAllowedOverRoaming(false);
		}
		// whether this download should be displayed in the system's Downloads UI
		request.setVisibleInDownloadsUi(true);
		// save apk address external file directory
		String apkName = url.substring(url.lastIndexOf("/") + 1, url.length());
		request.setDestinationInExternalFilesDir(this,
				Environment.DIRECTORY_DOWNLOADS, apkName);
		downloadId = downloadManager.enqueue(request);

		Log.i(TAG, "downloadApk download id:" + downloadId);
	}

	private void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(downLoadBroadcast = new DownLoadBroadcast(),
				intentFilter);
	}

	private void unregisterBroadcast() {
		if (downLoadBroadcast != null) {
			unregisterReceiver(downLoadBroadcast);
			downLoadBroadcast = null;
		}
	}

	private String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	private class DownLoadBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			long downId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent
					.getAction())) {
				if (downloadId == downId && downId != -1
						&& downloadManager != null) {
					Uri downIdUrl = downloadManager
							.getUriForDownloadedFile(downloadId);
					if (downIdUrl != null) {
						installAPk(new File(downIdUrl.getPath()));
					}
				}
			}
		}
	}

	private void installAPk(File apkFile) {
		// FIXME: Re-initialize the local configuration
		SharedPreferecesUtil.setLong(this,
				UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
				UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, 0);
		SharedPreferecesUtil.setLong(this,
				UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
				UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, 0);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// FIXME: if there is no set SDCard write access, or no SDCard, the apk
		// kept in memory, need to grant access to the installation
		try {
			String[] command = { "chmod", "777", apkFile.toString() };
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
		} catch (IOException ignored) {
		}
		intent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		stopSelf();
	}

}
