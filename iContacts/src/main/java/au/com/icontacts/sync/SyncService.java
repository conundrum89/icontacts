package au.com.icontacts.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Defines a Service that returns an IBinder for the SyncAdapter class, allowing
 * the SyncAdapter framework to call onPerformSync().
 */
public class SyncService extends Service {
    // Storage for an instance of the sync adapter
    private static SyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    /** Instantiates the sync adapter object, and disallows parallel syncs. */
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Returns an object that allows the system to invoke the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}