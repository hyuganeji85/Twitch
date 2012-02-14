package mk.t.android.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by IntelliJ IDEA.
 * User: tomislav.markovski
 * Date: 2/14/12
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncService extends IntentService {
    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
