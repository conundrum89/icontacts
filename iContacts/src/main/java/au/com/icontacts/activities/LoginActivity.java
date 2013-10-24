package au.com.icontacts.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import au.com.icontacts.R;

/**
 * Displays a pretty login page with fields for the username and password.
 * @author Matthew Rowland
 */

public class LoginActivity extends AccountAuthenticatorActivity
        implements  View.OnClickListener, TextView.OnEditorActionListener {
    private Button mLoginButton;
    private EditText mUsernameField;
    private EditText mPasswordField;

    // Account-related stuff
    public static final String AUTHORITY = "au.com.icontacts.provider";
    public static final String ACCOUNT_TYPE = "idashboard.com.au";
    public static final String ACCOUNT = "dummyaccount";
    private Account mAccount;

    // AuthenticatorActivity related stuff
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 5L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
            SECONDS_PER_MINUTE *
            MILLISECONDS_PER_SECOND;

    /** Sets the "GO" button on the soft keyboard to trigger a login event. */
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        if (action == EditorInfo.IME_ACTION_GO) {
            loginWithFormFields();
        }
        return false;
    }

    /** Sets the Go button of the form to trigger a login event. */
    public void onClick(View v) {
        loginWithFormFields();
    }



    /** Disables the title bar for the Login page. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = "Full access";

        // this will go elsewhere
        // ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, false);
        // ContentResolver.addPeriodicSync(mAccount, AUTHORITY, null, SYNC_INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        setContentView(R.layout.activity_login);

        // set the listeners on button/password/username.
        // Skip this page entirely if the user is already logged in.
    }

    /**
     * Gets the string values from the username and password form fields, and uses them
     * to attempt a login with the API.
     */
    private void loginWithFormFields() {
//        final String username = mUsernameField.getText().toString();
//        final String password = mPasswordField.getText().toString();
//
//        // new LoginTask(this).execute(username, password);
//        // TODO: Move this into a LoginTask class.
//        new AsyncTask<Void, Void, Intent>() {
//            @Override
//            protected Intent doInBackground(Void... params) {
////                String authToken = IDashApi.userSignIn(userName, password, mAuthTokenType);
//                final Intent result = new Intent();
//                result.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
//                result.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
////                result.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
//                result.putExtra(PARAM_USER_PASS, password);
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(Intent intent) {
//                finishLogin(intent);
//            }
//        }.execute();
    }

    private void finishLogin(Intent intent) {
        String username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String password = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        final Account account = new Account(username, accountType);
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = mAuthTokenType;
            // Creating the account on the device and setting the auth token we got
            mAccountManager.addAccountExplicitly(account, password, null);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            mAccountManager.setPassword(account, password);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
