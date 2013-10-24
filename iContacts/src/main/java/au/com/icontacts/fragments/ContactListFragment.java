package au.com.icontacts.fragments;

import android.support.v4.app.ListFragment;

/**
 * Displays a list of Contacts from an adapter of some kind.
 */
public class ContactListFragment extends ListFragment {
    // private CursorAdapter mAdapter;
    private String mContactType;

    public ContactListFragment() {
        this(null);
    }

    public ContactListFragment(String contactType) {
        super();
        mContactType = contactType;
    }
}