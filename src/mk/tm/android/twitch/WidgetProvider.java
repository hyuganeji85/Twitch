package mk.tm.android.twitch;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import mk.tm.android.twitch.services.MobileService;
import mk.tm.android.twitch.services.WifiService;

/**
 * Created by IntelliJ IDEA.
 * User: Tome
 * Date: 13.2.12
 * Time: 21:04
 * To change this template use File | Settings | File Templates.
 */
public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())
                || MobileService.MOBILE_CONNECTIVITY_CHANGED.equals(intent.getAction())) {
            updateMobile(context, MobileService.isMobileDataConnected(context));
        }

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

            int state = intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            updateWifi(context, state == WifiManager.WIFI_STATE_ENABLED
                    || state == WifiManager.WIFI_STATE_ENABLED);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (appWidgetIds.length > 0) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt("widget_id", appWidgetIds[0]) // yawn, too lazy to implement multiple widgets
                    .commit();
        }

        updateWifi(context, WifiService.isWifiEnabled(context));
        updateMobile(context, MobileService.isMobileDataConnected(context));
    }

    private void updateMobile(Context context, boolean mobileDataConnected) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        int widgetId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widget_id", -1);
        if (widgetId > 0) {
            views.setImageViewResource(R.id.mobileButton,
                    mobileDataConnected ? WidgetConfigure.getImageColorResource(context, WidgetConfigure.NETWORK_TYPE_MOBILE) : R.drawable.mobile_off);
        }
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void updateWifi(Context context, boolean wifiEnabled) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        int widgetId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widget_id", -1);
        if (widgetId > 0) {
            views.setImageViewResource(R.id.wifiButton,
                    wifiEnabled ? WidgetConfigure.getImageColorResource(context, WidgetConfigure.NETWORK_TYPE_WIFI) : R.drawable.wifi_off);
        }
        appWidgetManager.updateAppWidget(widgetId, views);
    }
}
