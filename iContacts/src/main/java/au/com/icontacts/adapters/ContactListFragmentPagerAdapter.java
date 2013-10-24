package au.com.icontacts.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import au.com.icontacts.fragments.ContactListFragment;

/**
 * FragmentPagerAdapter for the Contact List tabs. Instantiates a ContactListFragment
 * for each of the three list types needed.
 */
public class ContactListFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_COUNT = 3;

    public ContactListFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new ContactListFragment("vendor");
            case 1:
                return new ContactListFragment("landlord");
            case 2:
                return new ContactListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
