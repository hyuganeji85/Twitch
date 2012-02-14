package mk.t.android;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Tome
 * Date: 13.2.12
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */
public class MobileService extends IntentService {
    public static final String MOBILE_CONNECTIVITY_CHANGED = "mk.t.android.MOBILE_CONNECTIVITY_CHANGED";

    public MobileService() {
        super("MobileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setIndeterminate(intent);

        try {
            boolean isWifiEnabled = WifiService.isWifiEnabled(this);
            boolean isMobileEnabled = isMobileDataConnected(this);
            setMobileDataEnabled(this, !isMobileEnabled);

            // a hack to make widget update mobile data value
            // while wifi is enabled, disabling mobile data will not fire the connectivity change action.
            // why? go figure...
            if (isMobileEnabled && isWifiEnabled){
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Calendar when = Calendar.getInstance();
                when.add(Calendar.SECOND, 3);
                alarmManager.set(AlarmManager.RTC, when.getTimeInMillis(), PendingIntent.getBroadcast(this, 0, new Intent(MobileService.MOBILE_CONNECTIVITY_CHANGED), PendingIntent.FLAG_UPDATE_CURRENT));
            }
        } catch (Exception e) {
            Log.e("NetSwitcher", e.toString());
        }
    }

    private void setIndeterminate(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.widget_layout);
        int widgetId = PreferenceManager.getDefaultSharedPreferences(this).getInt("widget_id", -1);

        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mobile_indeterminate);
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.mobileButton, bitmap);
            appWidgetManager.updateAppWidget(widgetId, views);
            bitmap.recycle();
        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }

    public static boolean isMobileDataConnected(Context context) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
            setMobileDataEnabledMethod.setAccessible(true);

            return Boolean.valueOf(setMobileDataEnabledMethod.invoke(iConnectivityManager).toString());
        } catch (Exception e) {
            Log.e("NetSwitcher", e.toString());
        }
        return false;
    }

    public static void setMobileDataEnabledLegacy(Context context) {
        boolean isEnabled = false;
        Method dataConnSwitchmethod;
        Class telephonyManagerClass;
        Object ITelephonyStub;
        Class ITelephonyClass;

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
            isEnabled = true;
        } else {
            isEnabled = false;
        }
        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

            if (isEnabled) {
                dataConnSwitchmethod = ITelephonyClass
                        .getDeclaredMethod("disableDataConnectivity");
            } else {
                dataConnSwitchmethod = ITelephonyClass
                        .getDeclaredMethod("enableDataConnectivity");
            }
            dataConnSwitchmethod.setAccessible(true);
            dataConnSwitchmethod.invoke(ITelephonyStub);
        } catch (Exception e) {
            Log.e("NetSwitcher", e.toString());
        }
    }
}