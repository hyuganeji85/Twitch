package mk.t.android;

import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: tomislav.markovski
 * Date: 2/14/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WidgetProviderFull extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ("android.intent.action.SYNC_STATE_CHANGED".equals(intent.getAction())) {
            Log.d("TSwitch", "Action = android.intent.action.SYNC_STATE_CHANGED");
        }
        if ("com.android.sync.SYNC_CONN_STATUS_CHANGED".equals(intent.getAction())) {
            Log.d("TSwitch", "Action = com.android.sync.SYNC_CONN_STATUS_CHANGED");
        }
    }
}
