package au.com.icontacts.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import au.com.icontacts.R;
import au.com.icontacts.adapters.ContactListFragmentPagerAdapter;
import au.com.icontacts.adapters.LockableViewPager;
import au.com.icontacts.sync.IDashApi;

/**
 * Displays three lists of contacts for viewing Vendors, Landlords, or All Contacts.
 */
public class ContactListActivity extends ActionBarActivity
        implements  SearchView.OnQueryTextListener,
                    View.OnFocusChangeListener,
                    ActionBar.TabListener {

    private LockableViewPager mPager;
    private ActionBar mActionBar;
    private SearchView mSearchView;

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        // filter current tab
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        mPager = (LockableViewPager) findViewById(R.id.contact_list_pager);
        mPager.setAdapter(new ContactListFragmentPagerAdapter(getSupportFragmentManager()));
        mPager.setOnPageChangeListener(mPageChangeListener);

        mActionBar = getSupportActionBar();
        // Specify that tabs should be displayed in the action bar
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        // Setup and then hide the search field as a custom view on the ActionBar
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setCustomView(R.layout.fragment_action_bar_search);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        // Setup tabs
        mActionBar.addTab(mActionBar.newTab().setText(R.string.vendors).setTabListener(this));
        mActionBar.addTab(mActionBar.newTab().setText(R.string.landlords).setTabListener(this));
        mActionBar.addTab(mActionBar.newTab().setText(R.string.contacts).setTabListener(this));

        // Setup the SearchView itself, with the relevant listeners
        mSearchView = (SearchView) mActionBar.getCustomView().findViewById(R.id.action_bar_search);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);

        // Account stuff
        IDashApi.connect(this, "idashboard.com.au", "Full access");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeSearch();
                return true;
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_add_contact:
                openNewContactForm();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNewContactForm() {
        // Create ContactFormActivity intent
    }

    /** Replace the action bar tabs with the SearchView widget for filtering */
    private void openSearch() {
        // Prevent paging while tabs are hidden
        mPager.setPagingLocked(true);
        // Replace tabs with search bar, and focus it
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayShowCustomEnabled(true);
        mSearchView.requestFocus();
        showKeyboard();
        // Display the Home icon, for use as a "Back" button
        mActionBar.setDisplayShowHomeEnabled(true);
    }

    /** Do the reverse of everything done in the openSearch() method */
    private void closeSearch() {
        // Hide the Home icon
        mActionBar.setDisplayShowHomeEnabled(false);
        // Reset the SearchView to undo filtering, and replace it with tabs
        mSearchView.setQuery("", false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowCustomEnabled(false);
        // Re-enable paging
        mPager.setPagingLocked(false);
    }

    private void openSettings() {
        // Create SettingsActivity intent
    }

    private void openAbout() {
        // Create AboutActivity intent
    }

    /** Forces the soft keyboard to display */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }
}