package mk.tmarkovski.android.twitch.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by IntelliJ IDEA.
 * User: tomislav.markovski
 * Date: 2/14/12
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationService extends IntentService {
    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
