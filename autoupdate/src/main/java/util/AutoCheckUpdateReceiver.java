package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import update.UpdateModule;

/**
 * auto check update receiver, when update time come back will start up AutoCheckUpdateService service
 * @author sky
 */
public class AutoCheckUpdateReceiver extends BroadcastReceiver {
	public static final String RESET_XML_ACTION = "com.emporia.autoupdatelib.util.AutoCheckUpdateReceiver.reset_xml";
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (!UpdateModule.getInstance().isDestroy()) {
            AutoCheckUpdateService.startService(context);
        }
    }
}
