package au.com.icontacts.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import au.com.icontacts.R;
import au.com.icontacts.fragments.LoginDialogFragment;
import au.com.icontacts.sync.IDashApi;

/**
 * Displays a pretty login page with fields for the username and password.
 * @author Matthew Rowland
 */

public class LoginActivity extends AccountAuthenticatorFragmentActivity
        implements LoginDialogFragment.LoginDialogListener {

    // Account-related stuff
    public static final String AUTHORITY = "au.com.icontacts.provider";
    public static final String ACCOUNT_TYPE = "idashboard.com.au";

    // AuthenticatorActivity related stuff
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
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

    /** Disables the title bar for the Login page. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = "Full access";

        DialogFragment loginFragment = new LoginDialogFragment();
        loginFragment.setCancelable(false);
        loginFragment.show(getSupportFragmentManager(), "login");

        // this will go elsewhere
        // ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, false);
        // ContentResolver.addPeriodicSync(mAccount, AUTHORITY, null, SYNC_INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.activity_login);
    }

    /**
     * Gets the string values from the username and password form fields in the login dialog,
     * and uses them to attempt a login with the API. This method is called by the dialog
     * when the user presses the Login button, using the Listener callback method.
     */
    @Override
    public void onLoginClick(final LoginDialogFragment dialog) {
        final String username = dialog.usernameField.getText().toString();
        final String password = dialog.passwordField.getText().toString();

        dialog.usernameField.setVisibility(View.GONE);
        dialog.passwordField.setVisibility(View.GONE);
        dialog.loginActivityIndicator.setVisibility(View.VISIBLE);

        // new LoginTask(this).execute(username, password);
        // TODO: Move this into a LoginTask class.
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String authToken = IDashApi.userLogin(username, password, mAuthTokenType);
                final Intent result = new Intent();
                result.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
                result.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                result.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                result.putExtra(PARAM_USER_PASS, password);
                return result;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent, dialog);
            }
        }.execute();
    }

    private void finishLogin(Intent intent, LoginDialogFragment dialog) {
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        dialog.loginActivityIndicator.setVisibility(View.GONE);
        dialog.usernameField.setVisibility(View.VISIBLE);
        dialog.passwordField.setVisibility(View.VISIBLE);

        if (authToken != null) {
            String username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String password = intent.getStringExtra(PARAM_USER_PASS);
            String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            final Account account = new Account(username, accountType);
            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
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
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.invalid_login), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}