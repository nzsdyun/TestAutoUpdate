package update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

/**
 * this class is UpdateModule configuration, when you use automatic mode
 * check apk update, you need to init UpdateModule configuration
 * @author  sky
 */
public final class UpdateModuleConfiguration {
    /** local xml configuration constant filed */
    public static final String UPDATE_INTERVAL_TIME = "update_interval_time";
    public static final String CHECK_LAST_UPDATE_TIME = "check_last_update_time";
    public static final String CHECK_MAX_UPDATE_INTERVAL_TIME = "check_max_update_interval_time";
    /** every day check update's number of times, default is 4  */
    public static final int EVERY_DAY_CHECK_UPDATE_NUMBER_OF_TIME = 4;
    /** @see Context */
    final Context mContext;
    /** update configuration server location */
    final String mServerConfigurationUrl;
    /** each update inspection time */
    final long mCheckUpdateIntervalTime;
    /** each max update inspection time */
    final long mMaxCheckUpdateIntervalTime;
    /** start check update time */
    final long mStartCheckTime;
    /** end check update time */
    final long mEndCheckTime;

    private UpdateModuleConfiguration(final Builder build) {
        this.mContext = build.context;
        this.mServerConfigurationUrl = build.serverConfigurationUrl;
        this.mCheckUpdateIntervalTime = build.checkUpdateIntervalTime;
        this.mMaxCheckUpdateIntervalTime = build.maxCheckUpdateIntervalTime;
        this.mStartCheckTime = build.startCheckTime;
        this.mEndCheckTime = build.endCheckTime;
    }

    public static final class Builder {
        /**
         * default check update interval time
         */
        private static final long DEFAULT_CHECK_UPDATE_INTERVAL_TIME = 8 * 60 * 60 * 1000;
        /**
         * default max check update interval time
         */
        private static final long DEFAULT_MAX_CHECK_UPDATE_INTERVAL_TIME = 7 * 24 * 60 * 60 * 1000;
        /**
         * @see Context
         */
        private Context context;
        /**
         * update configuration server location
         */
        private String serverConfigurationUrl;
        /**
         * each update inspection time
         */
        private long checkUpdateIntervalTime = 0;
        /**
         * the longest check an update
         */
        private long maxCheckUpdateIntervalTime = 0;
        /**
         * start check whether update configuration time, default is 10:00
         */
        private long startCheckTime = 10 * 60;
        /**
         * end check whether update configuration time, default is 11:00
         */
        private long endCheckTime = 11 * 60;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * set server configuration url
         * @param serverConfigurationUrl update configuration server location
         * @return Builder
         */
        public Builder setServerConfigurationUrl(String serverConfigurationUrl) {
            this.serverConfigurationUrl = serverConfigurationUrl;
            return this;
        }

        /**
         * set check update application interval time, Unit of milliseconds
         * default check interval time value is 8 * 60 * 60 * 1000
         * @param checkUpdateIntervalTime check update interval time, Unit of milliseconds
         * @return Builder
         */
        public Builder setCheckUpdateIntervalTime(long checkUpdateIntervalTime) {
            this.checkUpdateIntervalTime = checkUpdateIntervalTime;
            return this;
        }

        /**
         * set max check update application interval time, Unit of milliseconds
         * default max check interval time value is a week (7 * 24 * 60 * 60 * 1000)
         * @param maxCheckUpdateIntervalTime
         * @return
         */
        public Builder setMaxCheckUpdateIntervalTime(long maxCheckUpdateIntervalTime) {
            this.maxCheckUpdateIntervalTime = maxCheckUpdateIntervalTime;
            return this;
        }
        /**
         * set start check update time
         * @param startTime the format is 10:00
         */
        public Builder setStartCheckTime(String startTime) {
        	if (!TextUtils.isEmpty(startTime)) {
        		 Pattern pattern = Pattern.compile("^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$");
        		 Matcher matcher = pattern.matcher(startTime);
        		 if (matcher.matches()) {
        			 String[] times = startTime.split(":");
        			 this.startCheckTime = Long.parseLong(times[0]) * 60 
        					 + Long.parseLong(times[1]);
        		 }
        	}
        	return this;
        }
        /**
         * set end check update time
         * @param startTime the format is 10:00
         */
        public Builder setEndCheckTime(String endTime) {
        	if (!TextUtils.isEmpty(endTime)) {
        		 Pattern pattern = Pattern.compile("^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$");
        		 Matcher matcher = pattern.matcher(endTime);
        		 if (matcher.matches()) {
        			 String[] times = endTime.split(":");
        			 this.endCheckTime = Long.parseLong(times[0]) * 60 
        					 + Long.parseLong(times[1]);
        		 }
        	}
        	return this;
        }

        /**
         * build UpdateModuleConfiguration object
         * @return UpdateModuleConfiguration
         */
        public UpdateModuleConfiguration build() {
            initEmptyValues();
            return new UpdateModuleConfiguration(this);
        }

        private void initEmptyValues() {
            if (context == null) {
                throw new NullPointerException("auto update context value is null");
            }
            if (TextUtils.isEmpty(serverConfigurationUrl)) {
                throw new NullPointerException("auto update server configuration url is null");
            }
            if (checkUpdateIntervalTime <= 0) {
                checkUpdateIntervalTime = DEFAULT_CHECK_UPDATE_INTERVAL_TIME;
            }
            if (maxCheckUpdateIntervalTime <= 0) {
                maxCheckUpdateIntervalTime = DEFAULT_MAX_CHECK_UPDATE_INTERVAL_TIME;
            }
        }
    }
}
