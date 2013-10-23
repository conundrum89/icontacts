package au.com.icontacts.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Handle the transfer of data between the iDashboard servers and this application,
 * using the Android sync adapter framework.
 * @author Matthew Rowland
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    /** Sets up the sync adapter */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        // I don't think I'll need a content resolver... but perform any setup here.
    }

    /**
     * Sets up the sync adapter. This form of the constructor maintains compatibility
     * with Android 3.0 and later platform versions.
     * TODO: How to support this without getting API-level errors?
     */
    // public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        // super(context, autoInitialize, allowParallelSyncs);
        // I don't think I'll need a content resolver... but perform any setup here.
    // }

    /**
     * Specifies the code to run in the sync adapter. The entire sync adapter runs in a
     * background thread.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        /**
         * Data transfer code from IDashAPI/BackgroundSync:
         *  - Connect to server
         *  - Download/upload data
         *  - Handle network errors
         *  - Handle data conflicts
         *  - Close connections, clean up temp files and caches
         */

    }

}
