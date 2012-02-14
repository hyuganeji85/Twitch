package mk.android;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by IntelliJ IDEA.
 * User: Tome
 * Date: 13.2.12
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class WifiService extends IntentService {
    public WifiService() {
        super("WifiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WifiManager wifiManager = ((WifiManager) this.getSystemService(Context.WIFI_SERVICE));
        wifiManager.setWifiEnabled(!isWifiEnabled(this));
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
        return wifiManager.isWifiEnabled();
    }
}
