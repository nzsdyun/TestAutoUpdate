package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * Network auxiliary tool
 * @author sky
 */
public class NetworkUtils {
	/**
	 * check network status
	 * @param context @see Context
	 * @return network connect return true, otherwise return false
	 */
	public static boolean isConnect(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			return ni.isConnected();
		}
		return false;
	}
	/**
	 * get network type
	 * @param context @see Context
	 * @return @see NetworkType
	 */
	public static NetworkType getNetworkType(Context context) {
		if (isConnect(context)) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			int type = ni.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return NetworkType.WIFI;
			} else {
				return NetworkType.OTHER;
			}
		}
		return NetworkType.NO;
	}
	
	public enum NetworkType {
		NO, OTHER, WIFI
	}
}
