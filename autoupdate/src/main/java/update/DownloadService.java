package update;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.example.sky.autoupdate.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
	private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
	private static final String TAG = DownloadService.class.getSimpleName();

	private static final int NOTIFICATION_ID = 0;

	private NotificationManager mNotifyManager;
	private Builder mBuilder;

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(this);

		String appName = getString(getApplicationInfo().labelRes);
		int icon = getApplicationInfo().icon;

		mBuilder.setContentTitle(appName).setSmallIcon(icon);
		String urlStr = intent.getStringExtra("download_url");
		InputStream in = null;
		FileOutputStream out = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setReadTimeout(10 * 1000);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");

			urlConnection.connect();
			long byteTotal = urlConnection.getContentLength();
			long byteSum = 0;
			int byteRead = 0;
			in = urlConnection.getInputStream();
			File dir = StorageUtils.getCacheDirectory(this);
//			// TODO: this is a stupid
//			String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1,
//					urlStr.length());
			String apkName = urlConnection.getURL().getFile().toString();
			File apkFile = new File(dir, apkName);
			out = new FileOutputStream(apkFile);
			byte[] buffer = new byte[BUFFER_SIZE];

			int oldProgress = 0;

			while ((byteRead = in.read(buffer)) != -1) {
				byteSum += byteRead;
				out.write(buffer, 0, byteRead);

				int progress = (int) (byteSum * 100L / byteTotal);
				// FIXME: if progress equals the schedule before, do not update,
				// if the update too frequently, otherwise it will cause the
				// interface card
				if (progress != oldProgress) {
					updateProgress(progress);
				}
				oldProgress = progress;
			}

			installAPk(apkFile);

			mNotifyManager.cancel(NOTIFICATION_ID);

		} catch (Exception e) {
			Log.e(TAG, "download apk file error");
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {

				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {

				}
			}
		}
	}

	private void updateProgress(int progress) {
		mBuilder.setContentText(
				this.getString(R.string.android_auto_update_download_progress,
						progress)).setProgress(100, progress, false);
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
				new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pendingintent);
		mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	private void installAPk(File apkFile) {
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

	}

}
