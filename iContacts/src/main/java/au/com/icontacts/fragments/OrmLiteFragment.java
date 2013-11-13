package au.com.icontacts.fragments;

import android.support.v4.app.Fragment;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import au.com.icontacts.helpers.DatabaseHelper;

/**
 * Extends Fragment with OrmLite helper methods.
 */
public class OrmLiteFragment extends Fragment {
    private DatabaseHelper mDatabaseHelper = null;

    protected DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }
}
