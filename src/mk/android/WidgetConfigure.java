package mk.android;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Created by IntelliJ IDEA.
 * User: Tome
 * Date: 13.2.12
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class WidgetConfigure extends Activity implements View.OnClickListener {
    private int mAppWidgetId;

    public static final int NETWORK_TYPE_WIFI = 0;
    public static final int NETWORK_TYPE_MOBILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.widget_configure);

        findViewById(R.id.sampleBlue).setOnClickListener(this);
        findViewById(R.id.samplePurple).setOnClickListener(this);
        findViewById(R.id.sampleGreen).setOnClickListener(this);
        findViewById(R.id.sampleRed).setOnClickListener(this);
        findViewById(R.id.sampleYellow).setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    public void onClick(View view) {

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("widget_id", mAppWidgetId)
                .putString("widget_color", view.getTag().toString())
                .commit();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(this.getPackageName(),
                R.layout.widget_layout);

        Intent wifiIntent = new Intent(this, WifiService.class);
        wifiIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        views.setOnClickPendingIntent(R.id.wifiButton, PendingIntent.getService(this, 0, wifiIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        Intent mobileIntent = new Intent(this, MobileService.class);
        mobileIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        views.setOnClickPendingIntent(R.id.mobileButton, PendingIntent.getService(this, 0, mobileIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        WifiManager wifiManager = ((WifiManager) this.getSystemService(Context.WIFI_SERVICE));
        views.setImageViewResource(R.id.wifiButton, wifiManager.isWifiEnabled()
                ? getImageColorResource(this, NETWORK_TYPE_WIFI) : R.drawable.wifi_off);

        views.setImageViewResource(R.id.mobileButton, MobileService.isMobileDataConnected(this)
                ? getImageColorResource(this, NETWORK_TYPE_MOBILE) : R.drawable.mobile_off);

        appWidgetManager.updateAppWidget(mAppWidgetId, views);


        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public static int getImageColorResource(Context context, int network_type) {
        String color = PreferenceManager.getDefaultSharedPreferences(context).getString("widget_color", "blue");
        if ("red".equals(color))
            return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_red_on : R.drawable.wifi_red_on;
        if ("blue".equals(color))
            return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_blue_on : R.drawable.wifi_blue_on;
        if ("green".equals(color))
            return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_green_on : R.drawable.wifi_green_on;
        if ("purple".equals(color))
            return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_purple_on : R.drawable.wifi_purple_on;
        if ("yellow".equals(color))
            return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_yellow_on : R.drawable.wifi_yellow_on;
        return NETWORK_TYPE_MOBILE == network_type ? R.drawable.mobile_blue_on : R.drawable.wifi_blue_on;
    }
}
