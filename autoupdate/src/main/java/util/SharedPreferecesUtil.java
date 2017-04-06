package util;

import android.content.Context;

/**
 * SharedPreferences tool you can save <code>boolean</code>, <code>int</code>,
 * <code>long</code> and <code>String</code> value
 * 
 * @author sky
 * 
 */
public class SharedPreferecesUtil {
	/**
	 * save boolean, mode default is Context.MODE_PRIVATE
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setBoolean(Context context, String name, String key,
			boolean value) {
		setBoolean(context, name, key, value, Context.MODE_PRIVATE);
	}

	/**
	 * save boolean
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 * @param mode
	 */
	public static void setBoolean(Context context, String name, String key,
			boolean value, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		android.content.SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * get boolean by key,mode default is Context.MODE_PRIVATE
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 *            if key is no exist, return <code>defValue</code>
	 * @return
	 */
	public static boolean getBoolean(Context context, String name, String key,
			boolean defValue) {
		return getBoolean(context, name, key, defValue, Context.MODE_PRIVATE);
	}

	/**
	 * get boolean
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @param mode
	 * @return
	 */
	public static boolean getBoolean(Context context, String name, String key,
			boolean defValue, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getBoolean(key, defValue);
	}

	/**
	 * save Integer value, mode default is Context.MODE_PRIVATE
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setInt(Context context, String name, String key,
			int value) {
		setInt(context, name, key, value, Context.MODE_PRIVATE);
	}

	/**
	 * save Integer value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 * @param mode
	 */
	public static void setInt(Context context, String name, String key,
			int value, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		android.content.SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * get Integer value, mode default is Context.MODE_PRIVATE
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String name, String key,
			int defValue) {
		return getInt(context, name, key, defValue, Context.MODE_PRIVATE);
	}

	/**
	 * get Integer value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @param mode
	 * @return
	 */
	public static int getInt(Context context, String name, String key,
			int defValue, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getInt(key, defValue);
	}

	/**
	 * save Long value,mode default is Context.MODE_PRIVATE
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setLong(Context context, String name, String key,
			long value) {
		setLong(context, name, key, value, Context.MODE_PRIVATE);
	}

	/**
	 * save Long value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 * @param mode
	 */
	public static void setLong(Context context, String name, String key,
			long value, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		android.content.SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * get Long value by default mode <Context.Mode_PRIVATE>
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String name, String key,
			long defValue) {
		return getLong(context, name, key, defValue, Context.MODE_PRIVATE);
	}

	/**
	 * get Long value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @param mode
	 * @return
	 */
	public static long getLong(Context context, String name, String key,
			long defValue, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getLong(key, defValue);
	}

	/**
	 * save String value by mode default <Context.MODE_PRIVATE>
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setString(Context context, String name, String key,
			String value) {
		setString(context, name, key, value, Context.MODE_PRIVATE);
	}

	/**
	 * save String value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 * @param mode
	 */
	public static void setString(Context context, String name, String key,
			String value, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		android.content.SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * get String value by default mode <Context.MODE_PRIVATE>
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String name, String key,
			String defValue) {
		return getString(context, name, key, defValue, Context.MODE_PRIVATE);
	}

	/**
	 * get String value
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defValue
	 * @param mode
	 * @return
	 */
	public static String getString(Context context, String name, String key,
			String defValue, int mode) {
		android.content.SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getString(key, defValue);
	}

}
