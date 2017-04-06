package util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import update.UpdateInfo;
import update.UpdateModule;
import update.UpdateModuleConfiguration;

/**
 * automatic update service, using the index backoff algorithm to calculate the next update interval
 * @author sky
 */
public class AutoCheckUpdateService extends IntentService {
    private static final String TAG = AutoCheckUpdateService.class.getSimpleName();
    private AlarmManager alarmManager;
    private PendingIntent checkPendingIntent;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AutoCheckUpdateService() {
        super("AutoCheckUpdateService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi") @Override
    protected void onHandleIntent(Intent intent) {
        long localMaxTime = SharedPreferecesUtil.getLong(this, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, 0);
        long triggerAtTime = SystemClock.elapsedRealtime()
                + UpdateModule.getInstance().checkUpdateIntervalTime();
        if (localMaxTime > 0) {
            triggerAtTime =  SystemClock.elapsedRealtime() + localMaxTime;
        }
        long lastUpdateTime = SharedPreferecesUtil.getLong(this, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                UpdateModuleConfiguration.CHECK_LAST_UPDATE_TIME, 0);
        long currentTime = System.currentTimeMillis();
        Log.i(TAG, "onHandleIntent: localMaxTime = " + localMaxTime + ", triggerAtTime = " + triggerAtTime
                + ", lastUpdateTime = " + lastUpdateTime + ", currentTime = " + currentTime);
        if ((currentTime - lastUpdateTime >= localMaxTime)
                && NetworkUtils.isConnect(this)
                && !UpdateModule.getInstance().isShowDialog()) {
            UpdateModule.getInstance().autoCheckUpdate();
        }
        alarmManager.cancel(checkPendingIntent);
        Intent i = new Intent(this, AutoCheckUpdateReceiver.class);
        checkPendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (isKitkat()) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, triggerAtTime, checkPendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, checkPendingIntent);
        }
    }

    /**
     * check the update of migration time next time
     * @param @see Context
     * @return next check update migration time
     */
    public static long retryUpdateIntervalTime(Context context) {
        long localMaxTime = SharedPreferecesUtil.getLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, 0);
        if (localMaxTime == 0) {
            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                    UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME,
                    UpdateModule.getInstance().checkUpdateIntervalTime());
            return SystemClock.elapsedRealtime() + UpdateModule.getInstance().checkUpdateIntervalTime();
        } else {
            //FIXME: doubled exponentially to reduce the number of checks
        	if (localMaxTime < UpdateModule.getInstance().checkUpdateIntervalTime()) {
        		localMaxTime = UpdateModule.getInstance().checkUpdateIntervalTime();
        	} else {
        		localMaxTime = localMaxTime * 2;
        	}
            if (localMaxTime >= UpdateModule.getInstance().checkMaxUpdateIntervalTime()) {
                localMaxTime = UpdateModule.getInstance().checkMaxUpdateIntervalTime();
            }
            SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                    UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, localMaxTime);
            return SystemClock.elapsedRealtime() + localMaxTime;
        }
    }
    
    public static long setCheckIntervalTimes(Context context, final UpdateInfo updateInfo) {
        long localMaxTime = SharedPreferecesUtil.getLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
                UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, 0);
        if (localMaxTime == 0) {
        	//FIXME : evert day check update's number of times
        	long startCheckTime = UpdateModule.getInstance().startCheckTime();
        	long endCheckTime = UpdateModule.getInstance().endCheckTime();
        	long times = UpdateModuleConfiguration.EVERY_DAY_CHECK_UPDATE_NUMBER_OF_TIME;
	        if (updateInfo != null && updateInfo.getCheckNumberOfTimes() > 0) {
	        	times = updateInfo.getCheckNumberOfTimes();
	        }
	        localMaxTime = ((endCheckTime - startCheckTime) / times) * 60 * 1000;
	        SharedPreferecesUtil.setLong(context, UpdateModuleConfiguration.UPDATE_INTERVAL_TIME,
	                UpdateModuleConfiguration.CHECK_MAX_UPDATE_INTERVAL_TIME, localMaxTime);
        }
        return localMaxTime;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }
    /** start AutoCheckUpdateService service */
    public static void startService(Context context) {
        Intent senderService = new Intent(context, AutoCheckUpdateService.class);
        context.startService(senderService);
    }
    /** android sdk version higher 19 */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
