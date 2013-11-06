package au.com.icontacts.sync;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import au.com.icontacts.R;
import au.com.icontacts.helpers.DatabaseHelper;
import au.com.icontacts.models.Contact;

/**
 * Handle the transfer of data between the iDashboard servers and this application,
 * using the Android sync adapter framework.
 * @author Matthew Rowland
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private SharedPreferences mPreferences;
    private Gson mGson;

    private final int PER_PAGE = 30;

    private int mContactPageCount = 1;
    private int mContactCount = 0;

    private DatabaseHelper mDatabaseHelper = null;

    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    /** Sets up the sync adapter */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext)
                .setContentText(mContext.getString(R.string.sync_title))
                .setSmallIcon(R.drawable.ic_launcher);
        mPreferences = mContext.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        // I don't think I'll need a content resolver... but perform any setup here.
    }

    /**
     * Sets up the sync adapter. This form of the constructor maintains compatibility
     * with Android 3.0 and later platform versions.
     * TODO: How to support this without getting API-level errors?
     */
//    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
//        super(context, autoInitialize, allowParallelSyncs);
//        // I don't think I'll need a content resolver... but perform any setup here.
//    }

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

        if (mPreferences.getLong("lastSync", 0) == 0) {
            fullSync();
        } else {
            // what
        }


        /**
         * Data transfer code from IDashAPI/BackgroundSync:
         *  - Connect to server
         *  - Download/upload data
         *  - Handle network errors
         *  - Handle data conflicts
         *  - Close connections, clean up temp files and caches
         */
    }

    /** Sync ALL the data! */
    private void fullSync() {
        setNotificationProgress("Preparing Sync", 0, true);

        try {
            loadContacts();
        } catch (JSONException e) {
            // TODO: Handle.
        }

        setLastSyncTime();
    }

    /** Loads contacts... */
    private void loadContacts() throws JSONException {
        RuntimeExceptionDao<Contact, Integer> dao = getHelper().getContactsDao();

        JSONObject contactPage;
        JSONArray contactList;
        int contactId;

        int progress = 0;

        // Grab the first page, then update mContactCount (and pageCount?) for subsequent loops.
        // This prevents us missing contacts if the number of pages changes during the sync process.
        for (int i = 1; i <= mContactPageCount; i++) {
            contactPage = IDashApi.getContactPage(i, PER_PAGE);
            mContactCount = contactPage.getInt("total_entries");
            mContactPageCount = contactPage.getInt("total_pages");
            contactList = contactPage.getJSONArray("contacts");
            for (int j = 0; j < contactList.length(); j++) {
                progress++;
                contactId = contactList.getJSONObject(j).getJSONObject("contact").getInt("id");
                if (contactId > 0) {
                    setNotificationProgress("Syncing contact " + progress + " of " + mContactCount, progress, false);
                    loadContact(contactId);
                }
            }
        }
    }

    private void loadContact(int contactId) {
        JSONObject jContact;
        Contact contact;

        // jContact = IDashApi.getContact(contactId);
        // contact = mGson.fromJson(jContact.toString(), Contact.class);
    }

    private void setLastSyncTime() {
        Date date = new Date(System.currentTimeMillis());
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong("lastSync", date.getTime());
        editor.commit();
    }

    void setNotificationProgress(String content, int progress, boolean indeterminate) {
        mBuilder.setContentText(content)
                .setProgress(mContactCount, progress, indeterminate);
        mNotificationManager.notify(0, mBuilder.build());
    }
}