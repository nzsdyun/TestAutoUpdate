package update;

/**
 * update information
 * @author sky
 */
public class UpdateInfo {
	private int versionCode;
	private String versionName;
	private String apkUrl;
	private String updateContent;
	private String updateTips;
	private boolean debugVersion;
	/** every day check update's number of times */
	private int checkNumberOfTimes;

	public UpdateInfo() {

	}

	public UpdateInfo(int versionCode, String versionName, String apkUrl,
			String updateContent, String updateTips, boolean debugVersion, int checkNumberOfTimes) {
		super();
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.apkUrl = apkUrl;
		this.updateContent = updateContent;
		this.updateTips = updateTips;
		this.debugVersion = debugVersion;
		this.checkNumberOfTimes = checkNumberOfTimes;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getUpdateContent() {
		return updateContent;
	}

	public void setUpdateContent(String updateContent) {
		this.updateContent = updateContent;
	}

	public String getUpdateTips() {
		return updateTips;
	}

	public void setUpdateTips(String updateTips) {
		this.updateTips = updateTips;
	}

	public boolean isDebugVersion() {
		return debugVersion;
	}

	public void setDebugVersion(boolean debugVersion) {
		this.debugVersion = debugVersion;
	}

	public int getCheckNumberOfTimes() {
		return checkNumberOfTimes;
	}

	public void setCheckNumberOfTimes(int checkNumberOfTimes) {
		this.checkNumberOfTimes = checkNumberOfTimes;
	}
	
	

}
