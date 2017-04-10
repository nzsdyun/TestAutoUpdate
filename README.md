### AutoUpdate library
> this document is a brief description of how to use the online update library has been how to write your updated configuration file has been uploaded your update apk.

##Version 1.0
#### how to use this library
1. your main project needs to rely on this library file. 
2. add the following code to the onCreate () and onDestroy () methods of the main activity:

<code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateModuleConfiguration updateModuleConfiguration = new UpdateModuleConfiguration.Builder(this)
                .setServerConfigurationUrl(UPDATE_URL)
                .setCheckUpdateIntervalTime(10 * 1000)
                .setMaxCheckUpdateIntervalTime(5 * 60 * 1000)
                .build();
        UpdateModule.getInstance().startAutoCheckUpdate(updateModuleConfiguration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateModule.getInstance().stopAutoCheckUpdate();
    }
</code>
#### how to write configuration file
1. you need to add your update in the service configuration file, which is a json file, its contents are as follows:

<code>

	{
		"update_info": {
			"version_code": 3,
			"version_name": "Launcher.apk",
			"apk_url": "http:www.emporia.com:8012/apk/launcher.apk",
			"update_content":"1. Bug fixes.\n2. Increase the calling function",
			"update_tips": "New feature updates"
		}
	}
</code>

> Note: to determine whether a local update apk is based on json configuration file "versionCode" field, when the configuration file is greater than the local apk versionCode will be prompted to update.

2. the "apk_url" in the configuration file is an address url for the apk that updates the server to be updated. The rest of the fields are prompted by the user's updated content.

#Version 2.0
> Version 2.0 has the following changes relative to version 1.0.

1. modify download manager, use system DownloadManager
2. modify update strategy, you can set check time every day.
3. increase the json file field "debug_version" for easy testing.
4. increase the json file field "check_number_times" for every day check update number of times, because default
every day check update number of times is 4.
5. the server update configuration file changes as follows:
<code>

		{
	 		"update_info": {
	 			"version_code": 1,
	 			"version_name": "emporia_launcher",
	 			"apk_url": "http://www.emporia.com/apk/emporia_launcher.apk",
	 			"update_content":"1. Bug fixes. \n 2. Increase the calling function",
	 			"update_tips": "New feature updates",
	 			"debug_version": false,
	 			"check_number_times": 5
	 		} 
		}
</code> 
6. use the way to change, you only need to work in the main activity <code>onResume()</code> method just the following method.


<code>

	     	@Override
     		protected void onResume() {
     			super.onResume();
    			UpdateModuleConfiguration updateModuleConfiguration = new UpdateModuleConfiguration.Builder(this)
     					.setServerConfigurationUrl(UPDATE_URL)
     					.setCheckUpdateIntervalTime(24 * 60 * 60 * 1000)
      					.setMaxCheckUpdateIntervalTime(15 * 24 * 60 * 60 * 1000)
			        	.setStartCheckTime("10:00")
			        	.setEndCheckTime("11:00")
      					.build();
      			UpdateModule.getInstance().startAutoCheckUpdate(updateModuleConfiguration);
     		}
</code>
7. you need to add the following configuration to the Manifest file.
<code>
	
        <!-- add update meta data -->
        <meta-data android:name="update_configuration_url" 
            android:value="http://ai.emporia-telecom.net/update_configuration/launcher_update.json" />

        <!-- auto update -->
        <service
            android:name="com.emporia.autoupdatelib.update.DownloadManagerService"
            android:exported="true" />
        <service android:name="com.emporia.autoupdatelib.util.AutoCheckUpdateService" />

        <receiver android:name="com.emporia.autoupdatelib.util.AutoCheckUpdateReceiver" />
</code>

>note: meta-date's name must is "update_configuration_url", the value is server update configuration path.

#### Update strategy
> the update strategy has two forms of support, one is the daily inspection time period, one is the check update interval.

1. Check the time period every day. This is through the application of the internal code <code>setStartCheckTime</code> and <code>setEndCheckTime</code> to specify what time during the day to check the update operation. the default is 10:00 to 11:00 this period of time to check, while the time period to check the number of updates also given a limit, the default number is 4, of course, you can through the server's json configuration file "check_number_times" Field to change this limit.

2. Check the interval of updates is the interval between the update dialog box and the next prompt update dialog box when the server has an updated apk. This is constrained by the setCheckUpdateIntervalTime and setMaxCheckUpdateIntervalTime methods of the application's internal code. Where the time interval for each check update is not constant, but incremented in multiples, such as the first check interval for the interval you set by setCheckUpdateIntervalTime, when the update dialog box appears, when you have not selected to download the update immediately , But choose to say later, then this time interval will be in the form of multiple increments.
> note: The setMaxCheckUpdateIntervalTime method determines the maximum interval for checking updates, that is, when the time interval is exceeded, the next update check will check whether the server is updated and defaults to one week.

3. These two forms of renewal are mutually influential, and decide whether or not to really check whether the server is updated is to satisfy both of these conditions. The first form takes precedence over the second form, but the second form in turn affects the first form. For example: when you set the daily 10:00 to 11:00 check update, not to say that this time within the time period will certainly go to the server to check. This depends on the current application of the check update interval. If you have rejected multiple checks, resulting in an update interval of more than one day (24 hours), then the next 10:00 to 11:00 The time period will not check the update.